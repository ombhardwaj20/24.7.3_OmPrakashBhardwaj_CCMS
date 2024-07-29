package com.card.Credit_card.services;

import com.card.Credit_card.dto.*;
import com.card.Credit_card.entities.CardStatement;
import com.card.Credit_card.entities.Transactions;
import com.card.Credit_card.exception.LimitExceededException;
import com.card.Credit_card.repository.TransactionsRepository;
import com.card.Credit_card.type.TransactionType;
import com.card.Credit_card.util.Utils;
import io.github.benas.randombeans.randomizers.range.LocalDateTimeRangeRandomizer;
import io.github.benas.randombeans.randomizers.time.ZoneOffsetRandomizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor

public class TransactionServiceImpl implements TransactionService{
    private final TransactionsRepository transactionsRepository;
    private final ModelMapper modelMapper;
    private final CardStatementService cardStatementService;

    private final EntityManager entityManager;

    /**
     * Add the transaction.
     *
     * @param addTransactionDTO the add transaction dto
     * @return the transaction dto
     */
    @Override
    @Transactional
    public TransactionDto addTransaction(UUID cardId, AddTransactionDto addTransactionDTO) throws LimitExceededException {
        logger.trace("Entered addTransaction");
        CardStatementDto statementDTO =
                cardStatementService.getOutstandingStatement(cardId);
        CardStatement cardStatement = modelMapper.map(statementDTO, CardStatement.class);
        BigDecimal totalDue = statementDTO.getTotalDue();
        totalDue = totalDue.add(addTransactionDTO.getAmount());
        statementDTO.setTotalDue(totalDue);
        if (totalDue.compareTo(statementDTO.getMaxAmount()) > 0) {
            throw new LimitExceededException("Maximum limit of the credit card is exceeded.");
        }

        BigDecimal minDue = totalDue.divide(BigDecimal.TEN, RoundingMode.CEILING);
        statementDTO.setMinDue(minDue);
        cardStatementService.updateCardStatement(statementDTO);

        if (addTransactionDTO.getTransactionDate() == null) {
            LocalDateTimeRangeRandomizer localDateTimeRangeRandomizer = LocalDateTimeRangeRandomizer
                    .aNewLocalDateTimeRangeRandomizer(LocalDateTime.now().minusMonths(5), LocalDateTime.now());
            ZoneOffsetRandomizer zoneOffsetRandomizer = ZoneOffsetRandomizer.aNewZoneOffsetRandomizer();
            OffsetDateTime randomPast = OffsetDateTime.of(localDateTimeRangeRandomizer.getRandomValue(),
                    zoneOffsetRandomizer.getRandomValue());
            addTransactionDTO.setTransactionDate(randomPast);
        }

        Transactions transaction = modelMapper.map(addTransactionDTO, Transactions.class);
        transaction.setCardStatementId(cardStatement);

        try {
            Currency currency = Currency.getInstance(addTransactionDTO.getCurrency());
            transaction.setCurrency(currency);
        } catch (IllegalArgumentException | NullPointerException exception) {
            logger.error("Invalid currency code.");
            throw new IllegalArgumentException("Invalid Currency Code.");
        }



        Transactions savedTransaction = transactionsRepository.save(transaction);
        logger.trace("Exited addTransaction");
        return modelMapper.map(savedTransaction, TransactionDto.class);
    }

    /**
     * Add payment transaction.
     *
     * @param cardId                the card id
     * @param paymentTransactionDTO the payment transaction dto
     * @return the transaction dto
     */
    @Override
    @Transactional
    public TransactionDto addPayment(UUID cardId, PaymentTransactionDto paymentTransactionDTO) {
        logger.trace("Entered addPayment");
        CardStatementDto statementDTO =
                cardStatementService.getOutstandingStatement(cardId);
        CardStatement cardStatement = modelMapper.map(statementDTO, CardStatement.class);
        BigDecimal paymentAmount = paymentTransactionDTO.getAmount();

        BigDecimal totalDue = statementDTO.getTotalDue();
        if (paymentAmount.compareTo(totalDue) > 0) {
            throw new IllegalArgumentException("Payment cannot be greater than outstanding due.");
        }

        BigDecimal minDue = statementDTO.getMinDue();
        if (minDue.compareTo(paymentAmount) > 0) {
            throw new IllegalArgumentException("The minimum due amount is: " + minDue);
        }

        totalDue = totalDue.subtract(paymentAmount);
        statementDTO.setTotalDue(totalDue);
        statementDTO.setSettleDate(OffsetDateTime.now());
        if (totalDue.compareTo(BigDecimal.ZERO) == 0) {
            minDue = BigDecimal.ZERO;
        } else {
            minDue = totalDue.divide(BigDecimal.TEN, RoundingMode.CEILING);
        }
        statementDTO.setMinDue(minDue);
        cardStatementService.updateCardStatement(statementDTO);

        Transactions transaction = modelMapper.map(paymentTransactionDTO, Transactions.class);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setTransactionDate(OffsetDateTime.now());
        transaction.setCardStatementId(cardStatement);


        try {
            Currency currency = Currency.getInstance(paymentTransactionDTO.getCurrency());
            transaction.setCurrency(currency);
        } catch (IllegalArgumentException | NullPointerException exception) {
            logger.error("Invalid currency code.");
            throw new IllegalArgumentException("Invalid Currency Code.");
        }

        Transactions savedTransaction = transactionsRepository.save(transaction);
        TransactionDto transactionDTO = modelMapper.map(savedTransaction, TransactionDto.class);

        AddCardStatementDto addCardStatementDTO = modelMapper.map(statementDTO, AddCardStatementDto.class);
        OffsetDateTime newDueDate = addCardStatementDTO.getDueDate().plusMonths(1);
        addCardStatementDTO.setDueDate(newDueDate);
        cardStatementService.addCardStatement(addCardStatementDTO);

        logger.trace("Exited addPayment");
        return transactionDTO;
    }

    /**
     * Add transaction statement list.
     *
     * @param cardId       the card id
     * @param month        the month
     * @param year         the year
     * @param transactions the transactions
     * @return the list
     */
    @Override
    public List<TransactionDto> addTransactionStatement(UUID cardId, int month, int year,
                                                        List<AddTransactionDto> transactions) throws LimitExceededException {
        logger.trace("Entered addTransactionStatement");
        logger.debug("Transactions count: " + transactions.size());
        List<TransactionDto> addedTransactions = new ArrayList<>();
        for (AddTransactionDto transactionDTO : transactions) {
            if (transactionDTO.getTransactionDate() == null) {
                OffsetDateTime dateTime = OffsetDateTime.of(LocalDate.of(year, month, 1), LocalTime.now(ZoneOffset.UTC), ZoneOffset.UTC);
                transactionDTO.setTransactionDate(dateTime);
            }
            TransactionDto addedTransaction = addTransaction(cardId, transactionDTO);
            addedTransactions.add(addedTransaction);
        }
        logger.trace("Exited addTransactionStatement");
        return addedTransactions;
    }

    /**
     * Gets transaction statement.
     *
     * @param cardId   the credit card id
     * @param month    the month
     * @param year     the year
     * @param pageable the pageable
     * @return the transaction statement
     */
    @Override
    public Page<TransactionDto> getTransactionStatement(UUID cardId, int month, int year,
                                                        Pageable pageable) {
        List<CardStatementDto> statements = cardStatementService.getCardStatementByCardId(cardId);
        List<CardStatement> cardStatements = Utils.mapList(modelMapper, statements, CardStatement.class);
        LocalDate localDate = LocalDate.of(year, month, 1)
                .with(TemporalAdjusters.firstDayOfMonth());
        OffsetDateTime start = OffsetDateTime.of(localDate, LocalTime.MIN, ZoneOffset.UTC);
        localDate = localDate.with(TemporalAdjusters.lastDayOfMonth());
        OffsetDateTime end = OffsetDateTime.of(localDate, LocalTime.MIN, ZoneOffset.UTC);
        Page<Transactions> all = transactionsRepository
                .findAllByCardStatementIdInAndTransactionDateBetween(cardStatements, start, end, pageable);
        return Utils.mapEntityPageIntoDtoPage(modelMapper, all, TransactionDto.class);
    }

}

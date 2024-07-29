package com.card.Credit_card.services;

import com.card.Credit_card.dto.AddTransactionDto;
import com.card.Credit_card.dto.PaymentTransactionDto;
import com.card.Credit_card.dto.TransactionDto;
import com.card.Credit_card.exception.LimitExceededException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;


public interface TransactionService {
    /**
     * Add the transaction.
     *
     * @param cardId            the card id
     * @param addTransactionDTO the add transaction dto
     * @return the transaction dto
     * @throws LimitExceededException the limit exceeded exception
     */
    TransactionDto addTransaction(UUID cardId, AddTransactionDto addTransactionDTO) throws LimitExceededException;

    /**
     * Add payment transaction.
     *
     * @param cardId                the card id
     * @param paymentTransactionDTO the payment transaction dto
     * @return the transaction dto
     */
    TransactionDto addPayment(UUID cardId, PaymentTransactionDto paymentTransactionDTO);

    /**
     * Add transaction statement list.
     *
     * @param cardId       the card id
     * @param month        the month
     * @param year         the year
     * @param transactions the transactions
     * @return the list
     * @throws LimitExceededException the limit exceeded exception
     */
    List<TransactionDto> addTransactionStatement(UUID cardId, int month, int year,
                                                 List<AddTransactionDto> transactions) throws LimitExceededException;

    /**
     * Gets transaction statement.
     *
     * @param cardId   the credit card id
     * @param month    the month
     * @param year     the year
     * @param pageable the pageable
     * @return the transaction statement
     */
    Page<TransactionDto> getTransactionStatement(UUID cardId, int month, int year, Pageable pageable);

}
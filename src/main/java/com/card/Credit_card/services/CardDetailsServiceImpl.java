package com.card.Credit_card.services;

import com.card.Credit_card.dto.*;
import com.card.Credit_card.entities.CardDetails;
import com.card.Credit_card.entities.User;
import com.card.Credit_card.repository.CardDetailsRepository;
import com.card.Credit_card.security.JwtUtil;
import com.card.Credit_card.util.Utils;
import io.github.benas.randombeans.randomizers.range.BigDecimalRangeRandomizer;
import io.github.benas.randombeans.randomizers.range.LocalDateTimeRangeRandomizer;
import io.github.benas.randombeans.randomizers.time.ZoneOffsetRandomizer;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardDetailsServiceImpl implements CardDetailsService{
    private final CardDetailsRepository cardDetailsRepository;
    private final CardStatementService cardStatementService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public Optional<CardDto> addCard(AddCardDto addCardDTO) {
        logger.trace("Entered addCard");
        CardDetails cardDetails = modelMapper.map(addCardDTO, CardDetails.class);
        String emailId = jwtUtil.getEmailFromSecurity();
        Optional<UserDto> userDTO = userService.getUserByEmailId(emailId);
        if (userDTO.isPresent()) {
            User user = modelMapper.map(userDTO.get(), User.class);
            cardDetails.setUser(user);
        }
        CardDetails savedCard = cardDetailsRepository.save(cardDetails);

        logger.trace("Adding initial card statement.");
        AddCardStatementDto addCardStatementDTO = new AddCardStatementDto();
        BigDecimalRangeRandomizer bigDecimalRangeRandomizer =
                BigDecimalRangeRandomizer.aNewBigDecimalRangeRandomizer(Double.valueOf(1_00_000.0),
                        Double.valueOf(10_00_000), Integer.valueOf(2));
        addCardStatementDTO.setMaxAmount(bigDecimalRangeRandomizer.getRandomValue());
        addCardStatementDTO.setMinDue(BigDecimal.ZERO);
        LocalDateTimeRangeRandomizer localDateTimeRangeRandomizer = LocalDateTimeRangeRandomizer
                .aNewLocalDateTimeRangeRandomizer(LocalDateTime.now(), LocalDateTime.now().plusMonths(10));
        ZoneOffsetRandomizer zoneOffsetRandomizer = ZoneOffsetRandomizer.aNewZoneOffsetRandomizer();
        OffsetDateTime randomFuture = OffsetDateTime.of(localDateTimeRangeRandomizer.getRandomValue(),
                zoneOffsetRandomizer.getRandomValue());
        addCardStatementDTO.setDueDate(randomFuture);
        addCardStatementDTO.setCardId(savedCard.getCardId());
        cardStatementService.addCardStatement(addCardStatementDTO);

        CardDto savedCardDTO = modelMapper.map(savedCard, CardDto.class);
        savedCardDTO.setDueDate(addCardStatementDTO.getDueDate());
        savedCardDTO.setTotalDue(addCardStatementDTO.getTotalDue());
        savedCardDTO.setMinDue(addCardStatementDTO.getMinDue());
        logger.trace("Exited addCard");
        return Optional.of(savedCardDTO);
    }

    @Override
    public Boolean isCardPresent(String cardNumber) {
        return cardDetailsRepository.findByCardNumber(cardNumber).isPresent();
    }

    @Override
    public Boolean isCardPresent(UUID cardId) {
        return cardDetailsRepository.findById(cardId).isPresent();
    }

    @Override
    public Page<CardDto> getAllCardsByCurrentUser(Pageable pageable) {
        logger.trace("Entered getAllCardsByCurrentUser");
        String emailId = jwtUtil.getEmailFromSecurity();
        Optional<UserDto> userDTO = userService.getUserByEmailId(emailId);
        userDTO.orElseThrow(() -> new UsernameNotFoundException("Email address not found."));
        User user = modelMapper.map(userDTO.get(), User.class);
        Page<CardDetails> cardDetailsList = cardDetailsRepository.findAllByUser(user, pageable);
        Page<CardDto> cardDetails = Utils.mapEntityPageIntoDtoPage(modelMapper, cardDetailsList, CardDto.class);
        for (CardDto card : cardDetails) {
            CardStatementDto outstandingStatement = cardStatementService.getOutstandingStatement(card.getCardId());
            card.setMinDue(outstandingStatement.getMinDue());
            card.setTotalDue(outstandingStatement.getTotalDue());
            card.setDueDate(outstandingStatement.getDueDate());
        }
        logger.trace("Exited getAllCardsByCurrentUser");
        return cardDetails;
    }

    @Override
    public Optional<CardDto> getCardById(UUID cardId) {
        logger.trace("Entered getCardByNumber");
        Optional<CardDetails> cardDetailsOptional = cardDetailsRepository.findById(cardId);
        if (cardDetailsOptional.isPresent()) {
            CardStatementDto outstandingStatement = cardStatementService.getOutstandingStatement(cardId);
            CardDto card = modelMapper.map(cardDetailsOptional.get(), CardDto.class);
            card.setMinDue(outstandingStatement.getMinDue());
            card.setTotalDue(outstandingStatement.getTotalDue());
            card.setDueDate(outstandingStatement.getDueDate());
            logger.trace("Exited getCardByNumber");
            return Optional.of(card);
        }
        logger.trace("Exited getCardByNumber");
        return Optional.empty();
    }

}

package com.card.Credit_card.services;

import com.card.Credit_card.dto.AddCardStatementDto;
import com.card.Credit_card.dto.CardStatementDto;
import com.card.Credit_card.entities.CardDetails;
import com.card.Credit_card.entities.CardStatement;
import com.card.Credit_card.repository.CardDetailsRepository;
import com.card.Credit_card.repository.CardStatementRepository;
import com.card.Credit_card.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j

public class CardStatementServiceImpl implements CardStatementService{
    private final CardStatementRepository cardStatementRepository;
    private final ModelMapper modelMapper;
    private final CardDetailsRepository cardDetailsRepository;

    @Override
    public Optional<CardStatementDto> addCardStatement(AddCardStatementDto addCardStatementDTO) {
        logger.trace("Entered addCardStatement");
        CardStatement cardStatement = modelMapper.map(addCardStatementDTO, CardStatement.class);
        CardStatement savedCardStatement = cardStatementRepository.save(cardStatement);
        CardStatementDto cardStatementDTO = modelMapper.map(savedCardStatement, CardStatementDto.class);
        logger.trace("Exited addCardStatement");
        return Optional.of(cardStatementDTO);
    }

    /**
     * Update card statement.
     *
     * @param cardStatementDTO the card statement dto
     * @return the card statement dto
     */
    @Override
    public CardStatementDto updateCardStatement(CardStatementDto cardStatementDTO) {
        logger.trace("Entered updateCardStatement");
        CardStatement cardStatement = modelMapper.map(cardStatementDTO, CardStatement.class);
        CardStatement savedCardStatement = cardStatementRepository.save(cardStatement);
        CardStatementDto updatedCardStatementDTO = modelMapper.map(savedCardStatement, CardStatementDto.class);
        logger.trace("Exited updateCardStatement");
        return updatedCardStatementDTO;
    }

    /**
     * Gets card statement by card id.
     *
     * @param cardId the card id
     * @return the card statement by card id
     */
    @Override
    public List<CardStatementDto> getCardStatementByCardId(UUID cardId) {
        CardDetails card = new CardDetails();
        card.setCardId(cardId);
        return Utils.mapList(modelMapper, cardStatementRepository.findAllByCardId(card), CardStatementDto.class);
    }

    @Override
    public CardStatementDto getOutstandingStatement(UUID cardId) {
        logger.trace("Entered getOutstandingStatement");
        Optional<CardDetails> cardDetails = cardDetailsRepository.findById(cardId);
        cardDetails.orElseThrow(() -> new IllegalArgumentException("Card Id not found."));
        CardStatement outstandingStatement =
                cardStatementRepository.findCardStatementBySettleDateIsNullAndCardId(cardDetails.get());
        CardStatementDto cardStatementDTO = modelMapper.map(outstandingStatement, CardStatementDto.class);
        cardStatementDTO.setCardId(cardId);
        logger.trace("Exited getOutstandingStatement");
        return cardStatementDTO;
    }
}

package com.card.Credit_card.services;

import com.card.Credit_card.dto.AddCardStatementDto;
import com.card.Credit_card.dto.CardStatementDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardStatementService {
    /**
     * Add card statement optional.
     *
     * @param addCardStatementDTO the add card statement dto
     * @return the optional
     */
    Optional<CardStatementDto> addCardStatement(AddCardStatementDto addCardStatementDTO);

    /**
     * Update card statement.
     *
     * @param cardStatementDTO the card statement dto
     * @return the card statement dto
     */
    CardStatementDto updateCardStatement(CardStatementDto cardStatementDTO);


    /**
     * Gets card statement by card id.
     *
     * @param cardId the card id
     * @return the card statement by card id
     */
    List<CardStatementDto> getCardStatementByCardId(UUID cardId);

    /**
     * Gets outstanding statement.
     *
     * @param cardId the card id
     * @return the outstanding statement
     */
    CardStatementDto getOutstandingStatement(UUID cardId);
}

package com.card.Credit_card.services;

import com.card.Credit_card.dto.AddCardDto;
import com.card.Credit_card.dto.CardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;


public interface CardDetailsService {
    /**
     * Add card.
     *
     * @param addCardDTO the add card dto
     * @return the card details if added successfully.
     */
    Optional<CardDto> addCard(AddCardDto addCardDTO);

    /**
     * Checks if the card exists with the given card number or not.
     *
     * @param cardNumber the card number
     * @return the boolean
     */
    Boolean isCardPresent(String cardNumber);

    /**
     * Checks if card is present or not with the given card id.
     *
     * @param cardId the card id
     * @return the boolean
     */
    Boolean isCardPresent(UUID cardId);


    /**
     * Gets all cards of the logged in user.
     *
     * @return the all cards of the current user
     */
    Page<CardDto> getAllCardsByCurrentUser(Pageable pageable);

    /**
     * Gets card by id.
     *
     * @param cardId the card id
     * @return the card by id
     */
    Optional<CardDto> getCardById(UUID cardId);
}

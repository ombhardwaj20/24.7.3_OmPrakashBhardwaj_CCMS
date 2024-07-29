package com.card.Credit_card.repository;


import com.card.Credit_card.entities.CardDetails;
import com.card.Credit_card.entities.CardStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardStatementRepository extends JpaRepository<CardStatement, UUID>{
    /**
     * Find card statement of a card which is not settled.
     *
     * @param cardId the card id
     * @return the card statement
     */
    CardStatement findCardStatementBySettleDateIsNullAndCardId(CardDetails cardId);

    /**
     * Find all by card statements id.
     *
     * @param cardId the card id
     * @return the list
     */
    List<CardStatement> findAllByCardId(CardDetails cardId);
}

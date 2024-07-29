package com.card.Credit_card.type;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor

public enum TransactionType {
    /**
     * Debit transaction type.
    DEBIT("debit");*/
    /**
     * Credit transaction type.
     */
    CREDIT("credit");

    private final String code;
}

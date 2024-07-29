package com.card.Credit_card.type;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum CardPaymentService {
    /**
     * American express card payment service.
     */
    AMERICAN_EXPRESS("AMEX"),
    /**
     * Visa card payment service.
     */
    VISA("VISA"),
    /**
     * Master card card payment service.
     */
    MASTER_CARD("MTC"),
    /**
     * Maestro card payment service.
     */
    MAESTRO("MAS"),
    /**
     * Discover card payment service.
     */
    DISCOVER("DIS"),
    /**
     * Rupay card payment service.
     */
    RUPAY("RUPAY");
    private final String code;
}

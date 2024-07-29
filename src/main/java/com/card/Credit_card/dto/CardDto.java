package com.card.Credit_card.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
@Getter
@Setter
public class CardDto {

    private UUID cardId;
    private String cardNickName;
    private String cardNumber;
    private String nameOnCard;
    private String expiryDate;
    private String cardPaymentService;
    private OffsetDateTime dueDate;
    private BigDecimal totalDue;
    private BigDecimal minDue;
}

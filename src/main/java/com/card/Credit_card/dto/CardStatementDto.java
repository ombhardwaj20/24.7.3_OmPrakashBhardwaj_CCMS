package com.card.Credit_card.dto;

import com.card.Credit_card.entities.Transactions;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class CardStatementDto {
    private UUID cardStatementId;
    private BigDecimal minDue;
    private BigDecimal totalDue;
    private BigDecimal maxAmount;
    private OffsetDateTime dueDate;
    private OffsetDateTime settleDate;
    private UUID cardId;
    private List<Transactions> transactions;
}

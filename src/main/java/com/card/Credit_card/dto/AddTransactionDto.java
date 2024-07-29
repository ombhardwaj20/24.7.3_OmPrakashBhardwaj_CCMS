package com.card.Credit_card.dto;


import com.card.Credit_card.type.TransactionType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class AddTransactionDto {
    @NotNull(message = "Amount is mandatory.")
    private BigDecimal amount;

    private String currency = "INR";

    private TransactionType transactionType;

    private OffsetDateTime transactionDate;

    @NotBlank(message = "Category is mandatory.")
    private String category;

    @NotBlank(message = "Vendor is mandatory.")
    private String vendor;
}

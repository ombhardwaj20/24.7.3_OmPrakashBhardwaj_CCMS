package com.card.Credit_card.dto;




import com.card.Credit_card.type.CardPaymentService;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Getter
@Setter
public class AddCardDto {
    @NotBlank(message = "Card Nick Name is mandatory.")
    private String cardNickName;
    @NotBlank(message = "Card Number is mandatory")
    private String cardNumber;
    @NotBlank(message = "Name on card is mandatory.")
    private String nameOnCard;
    @NotBlank(message = "Expiry date is mandatory.")
    private String expiryDate;
    @NotBlank(message = "CVV is mandatory.")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV should be 3 or 4 digits long and should only contain numbers.")
    private String cvv;
    private CardPaymentService cardPaymentService;
}

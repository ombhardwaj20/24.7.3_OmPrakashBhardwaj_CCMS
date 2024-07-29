package com.card.Credit_card.dto;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.UUID;


@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class UserDto {
    private UUID userId;
    private String emailId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    @Nullable
    private String token;
    @Nullable
    private String tokenType;
    private boolean isActive;
}


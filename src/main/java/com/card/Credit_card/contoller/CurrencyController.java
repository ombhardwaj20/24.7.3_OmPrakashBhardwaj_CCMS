package com.card.Credit_card.contoller;

import com.card.Credit_card.dto.CurrencyDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@Slf4j
@CrossOrigin
@RequestMapping("/currency")
@SwaggerDefinition(
        info = @Info(description = "Currency Operation", title = "Currency Controller", version = "1.0")
)
public class CurrencyController {
    /**
     * Gets the list of currencies.
     *
     * @return the currencies
     */
    @ApiResponses(
            @ApiResponse(code = HttpServletResponse.SC_OK,
                    message = "Currencies returned successfully.", response = CurrencyDto.class)
    )
    @ApiOperation(value = "Gets the list of currencies", produces = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization("JWT")})
    @GetMapping
    public List<CurrencyDto> getCurrencies() {
        return Currency.getAvailableCurrencies()
                .stream()
                .map(currency -> new CurrencyDto(currency.getCurrencyCode(),
                        currency.getSymbol(),
                        currency.getDisplayName()))
                .sorted()
                .collect(Collectors.toList());
    }
}

package com.card.Credit_card.contoller;

import com.card.Credit_card.annotaion.ApiPageable;
import com.card.Credit_card.configuration.SpringFoxConfiguration;
import com.card.Credit_card.dto.AddTransactionDto;
import com.card.Credit_card.dto.PaymentTransactionDto;
import com.card.Credit_card.dto.TransactionDto;
import com.card.Credit_card.exception.LimitExceededException;
import com.card.Credit_card.model.ErrorDetails;
import com.card.Credit_card.services.CardDetailsService;
import com.card.Credit_card.services.TransactionService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@Api(tags = SpringFoxConfiguration.CARD_TAG)
@SwaggerDefinition(
        info = @Info(description = "Transaction Operations", title = "Transaction Controller", version = "1.0")
)
public class TransactionController {
    private final TransactionService transactionService;
    private final CardDetailsService cardDetailsService;

    @ApiOperation(value = "Adds a single transaction to the credit card.", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization("JWT")})
    @ApiResponses({
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Successfully added the transaction",
                    response = TransactionDto.class),
            @ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Credit card not found.",
                    response = ErrorDetails.class)
    })
    @PostMapping(value = "/cards/{id}/transaction", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addTransaction(@PathVariable(value = "id")
                                            @ApiParam(value = "credit card id", required = true)
                                            UUID cardId,
                                            @RequestBody @Valid AddTransactionDto addTransactionDTO) {
        if (cardId == null)
            return ResponseEntity.badRequest().body(
                    new ErrorDetails(HttpStatus.BAD_REQUEST, "Card Id is mandatory.")
            );
        if (!cardDetailsService.isCardPresent(cardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorDetails(HttpStatus.NOT_FOUND, "Credit card not found.")
            );
        }
        TransactionDto transactionDTO;
        try {
            transactionDTO = transactionService.addTransaction(cardId, addTransactionDTO);
        } catch (LimitExceededException e) {
            ErrorDetails details = new ErrorDetails(HttpStatus.OK, e.getMessage());
            return ResponseEntity.ok(details);
        }
        return ResponseEntity.created(URI.create("/transaction/" + transactionDTO.getTransactionId()))
                .body(transactionDTO);
    }

    @PostMapping(value = "/cards/{id}/pay", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPayment(@PathVariable(value = "id")
                                        @ApiParam(value = "credit card id", required = true)
                                        UUID cardId,
                                        @RequestBody @Valid PaymentTransactionDto paymentTransactionDTO) {
        logger.trace("Entered addPayment");
        if (cardId == null) {
            logger.trace("Exited addPayment");
            return ResponseEntity.badRequest().body(
                    new ErrorDetails(HttpStatus.BAD_REQUEST, "Card Id is mandatory.")
            );
        }
        if (!cardDetailsService.isCardPresent(cardId)) {
            logger.trace("Exited addPayment");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorDetails(HttpStatus.NOT_FOUND, "Credit card not found.")
            );
        }
        TransactionDto transactionDTO = transactionService.addPayment(cardId, paymentTransactionDTO);
        logger.trace("Exited addPayment");
        return ResponseEntity.created(URI.create("/transaction/" + transactionDTO.getTransactionId()))
                .body(transactionDTO);
    }

    @ApiResponses({
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Successfully added the statement.",
                    response = TransactionDto.class),
            @ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Credit card not found.",
                    response = ErrorDetails.class)
    })
    @ApiOperation(value = "Adds transactions to the specified credit card.", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization("JWT")})
    @PostMapping(value = "/cards/{id}/statements/{year}/{month}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addTransactionStatement(@PathVariable(value = "id")
                                                     @ApiParam(value = "credit card id", required = true)
                                                     UUID cardId,
                                                     @ApiParam(value = "month", required = true)
                                                     @PathVariable int month,
                                                     @ApiParam(value = "year", required = true)
                                                     @PathVariable int year,
                                                     @RequestBody @Valid List<AddTransactionDto> transactions) {
        if (!cardDetailsService.isCardPresent(cardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorDetails(HttpStatus.NOT_FOUND, "Credit card not found.")
            );
        }
        List<TransactionDto> transactionDTOS;
        try {
            transactionDTOS = transactionService.addTransactionStatement(cardId, month, year, transactions);
        } catch (LimitExceededException e) {
            ErrorDetails details = new ErrorDetails(HttpStatus.OK, e.getMessage());
            return ResponseEntity.ok(details);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDTOS);
    }


    @ApiResponses({
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Successfully generated the card statement.",
                    response = TransactionDto.class),
            @ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Credit card not found.",
                    response = ErrorDetails.class)
    })
    @ApiOperation(value = "Gets the credit card statement of the given month and year.",
            produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization("JWT")})
    @GetMapping(value = "/cards/{id}/statements/{year}/{month}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiPageable
    public ResponseEntity<?> getTransactionStatement(@PathVariable(value = "id")
                                                     @ApiParam(value = "credit card id", required = true)
                                                     UUID cardId,
                                                     @ApiParam(value = "month", required = true)
                                                     @PathVariable int month,
                                                     @ApiParam(value = "year", required = true)
                                                     @PathVariable int year,
                                                     @PageableDefault @ApiIgnore Pageable pageable) {
        logger.trace("Entered getTransactionStatement");
        if (!cardDetailsService.isCardPresent(cardId)) {
            logger.debug("Credit Card with id {} not found.", cardId);
            logger.trace("Exited getTransactionStatement");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorDetails(HttpStatus.NOT_FOUND, "Credit card not found.")
            );
        }
        Page<TransactionDto> statement =
                transactionService.getTransactionStatement(cardId, month, year, pageable);
        logger.trace("Exited getTransactionStatement");
        return ResponseEntity.ok(statement);
    }

}
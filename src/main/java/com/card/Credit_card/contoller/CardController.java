package com.card.Credit_card.contoller;


import com.card.Credit_card.annotaion.ApiPageable;
import com.card.Credit_card.configuration.SpringFoxConfiguration;
import com.card.Credit_card.dto.AddCardDto;
import com.card.Credit_card.dto.CardDto;
import com.card.Credit_card.model.ErrorDetails;
import com.card.Credit_card.services.CardDetailsService;
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
import java.util.Optional;
import java.util.UUID;


@RestController
@Validated
@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@Api(tags = SpringFoxConfiguration.CARD_TAG)
@SwaggerDefinition(
        info = @Info(description = "Card Operations", title = "Card Controller", version = "1.0")
)
public class CardController {
    private final CardDetailsService cardDetailsService;

    /**
     * Add a new card.
     *
     * @param cardDTO the details of the card
     * @return the response entity
     */
    @ApiResponses({
            @ApiResponse(code = HttpServletResponse.SC_CREATED,
                    message = "Card added successfully.", response = CardDto.class),
            @ApiResponse(code = HttpServletResponse.SC_CONFLICT,
                    message = "Card already added", response = ErrorDetails.class)
    })
    @ApiOperation(value = "Adds a new card with the given details.",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization("JWT")})
    @PostMapping(value = "/cards", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCard(@Valid @RequestBody AddCardDto cardDTO) {
        logger.trace("Entered addCard");
        if (cardDetailsService.isCardPresent(cardDTO.getCardNumber()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorDetails(HttpStatus.CONFLICT, "Card already exists."));
        Optional<CardDto> cardResponseDTO = cardDetailsService.addCard(cardDTO);
        if (cardResponseDTO.isPresent()) {
            CardDto card = cardResponseDTO.get();
            logger.trace("Exited addCard");
            return ResponseEntity.created(URI.create("/cards/" + card.getCardId())).body(card);
        }
        logger.trace("Exited addCard");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Gets the list of all cards of the logged in user.
     *
     * @return the response entity
     */
    @ApiResponses({
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Cards returned successfully.",
                    response = CardDto.class)
    })
    @ApiOperation(value = "Gets the list of all cards of the logged in user.", produces = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization("JWT")})
    @GetMapping(value = "/cards", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiPageable
    public ResponseEntity<?> getAllCardsByCurrentUser(@PageableDefault @ApiIgnore Pageable pageable) {
        logger.trace("Entered getAllCardsByCurrentUser");
        Page<CardDto> cards = cardDetailsService.getAllCardsByCurrentUser(pageable);
        logger.trace("Exited getAllCardsByCurrentUser");
        return ResponseEntity.ok(cards);
    }

    @ApiResponses({
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Card details returned successfully.", response = CardDto.class),
            @ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "Card Id not found.")
    })
    @ApiOperation(value = "Gets the card by card id.", produces = MediaType.APPLICATION_JSON_VALUE,
            authorizations = {@Authorization("JWT")})
    @GetMapping(value = "/cards/{cardId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCardById(@PathVariable
                                         @ApiParam(value = "credit card id", readOnly = true)
                                         UUID cardId) {
        logger.trace("Entered getCardById");
        Optional<CardDto> cardByNumber = cardDetailsService.getCardById(cardId);
        if (cardByNumber.isPresent()) {
            logger.trace("Exited getCardById");
            return ResponseEntity.ok(cardByNumber.get());
        }
        logger.trace("Exited getCardById");
        return ResponseEntity.noContent().build();
    }
}
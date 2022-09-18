package com.testaarosa.springRecallBookApp.recipient.controller;

import com.testaarosa.springRecallBookApp.globalHeaderFactory.HeaderKey;
import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientResponse;
import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.testaarosa.springRecallBookApp.globalHeaderFactory.HttpHeaderFactory.getSuccessfulHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/recipients")
@RequiredArgsConstructor
@Validated
@Tag(name = "Recipients API", description = "API designed to manipulate recipient object")
public class RecipientController {
    private final RecipientUseCase recipientUseCase;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all recipients", description = "Get all recipients from data base")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No recipients found!"),
    })
    public ResponseEntity<List<Recipient>> getAll(@RequestParam Optional<String> email,
                                                  @RequestParam(value = "limit", defaultValue = "3", required = false) int limit) {
        return email
                .map(s -> prepareResponseForGetAll(recipientUseCase.getAllRecipientsByEmail(s)))
                .orElseGet(() -> prepareResponseForGetAll(recipientUseCase.findAll()
                        .stream()
                        .limit(limit)
                        .collect(Collectors.toList())));
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get recipient by ID", description = "Get recipient by ID from data base")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No recipient found!"),
    })
    public ResponseEntity<Recipient> getRecipientById(@PathVariable("id") @NotNull(message = "Recipient ID filed can't be null")
                                                      @Min(value = 1, message = "Recipient ID field value must be greater than 0") Long id) {
        return recipientUseCase.findById(id)
                .map(recipient -> ResponseEntity.ok()
                        .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                        .body(recipient))
                .orElse(ResponseEntity.notFound()
                        .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NOT_FOUND.name())
                        .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), "Recipient with ID: " + id + " not found!")
                        .build());
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Add new recipient", description = "Add new recipient. All fields are validated")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recipient object created successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
            @ApiResponse(responseCode = "404", description = "Recipient not found!"),
    })
    public ResponseEntity<Void> addRecipient(@RequestBody @Validated({SaveRecipientGroup.class}) RestRecipientCommand command) {
        Recipient savedRecipient = recipientUseCase.addRecipient(command.toSaveRecipientCommand());
        URI uri = getUri(savedRecipient.getId());
        return ResponseEntity.created(uri)
                .headers(getSuccessfulHeaders(HttpStatus.CREATED, HttpMethod.POST))
                .build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update recipient", description = "Update recipient. All fields are validated")
    @Parameter(name = "id", required = true, description = "Updating recipient ID")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Recipient updated successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
    })
    public ResponseEntity<?> updateRecipient(@PathVariable("id") @NotNull(message = "RecipientID filed can't be null")
                                             @Min(value = 1, message = "RecipientId field value must be greater then 0") Long id,
                                             @RequestBody @Validated(UpdateRecipientGroup.class) RestRecipientCommand command) {
        RecipientResponse recipientResponse = recipientUseCase.updateRecipient(command.toUpdateRecipientCommand(id));
        if (!recipientResponse.isSuccess()) {
            return ResponseEntity.notFound()
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.PATCH.name())
                    .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NOT_FOUND.name())
                    .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), recipientResponse.getErrorList().toString())
                    .build();
        }
        return ResponseEntity.created(getUri(recipientResponse.getOrderId()))
                .headers(getSuccessfulHeaders(HttpStatus.ACCEPTED, HttpMethod.PATCH))
                .build();

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove recipient", description = "Remove recipient by ID")
    @Parameter(name = "id", required = true, description = "Remove by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Recipiet removed successful"),
    })
    public void removeRecipientById(@PathVariable("id") @NotNull(message = "RecipientId filed can't be null")
                                    @Min(value = 1, message = "RecipientId field value must be greater than 0") Long id) {
        recipientUseCase.removeRecipientById(id);

    }

    private ResponseEntity<List<Recipient>> prepareResponseForGetAll(List<Recipient> recipientList) {
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                .body(recipientList);
    }

    //todo cos zrobic z tym URI
    private static URI getUri(Long id) {
        return ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/recipient")
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}

package com.testaarosa.springRecallBookApp.author.controller;

import com.testaarosa.springRecallBookApp.author.application.port.AuthorUseCase;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.globalHeaderFactory.HeaderKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static com.testaarosa.springRecallBookApp.globalHeaderFactory.HttpHeaderFactory.getSuccessfulHeaders;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authors API", description = "API designed to manipulate the Author object")
class AuthorController {
    private final AuthorUseCase authorUseCase;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all authors from data base",
            description = "Filtering by name or/and lastName or/and yearOfBirth. Is not case sensitive. Limit default 3 nor required")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No books found!"),
    })
    public ResponseEntity<List<Author>> findALl(@RequestParam Optional<String> name,
                                                @RequestParam Optional<String> lastName,
                                                @RequestParam Optional<Integer> yearOfBirth,
                                                @RequestParam(value = "limit", defaultValue = "3", required = false) int limit) {

        return prepareResponseForGetAll(authorUseCase.findAllByParams(AuthorQueryCommand.builder()
                .name(name)
                .lastName(lastName)
                .yearOfBirth(yearOfBirth)
                .limit(limit).build()));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get author by id from data base", description = "Search for one author by data base unique ID")
    @Parameter(name = "id", required = true, description = "Searching author ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No author found!"),
    })
    public ResponseEntity<Author> findOneById(@PathVariable("id")
                                              @NotNull(message = "AuthorId filed can't be null")
                                              @Min(value = 1, message = "BookId field value must be greater than 0") Long id) {
        return authorUseCase.findById(id)
                .map(author -> ResponseEntity.ok()
                        .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                        .body(author))
                .orElseGet(() -> ResponseEntity.notFound()
                        .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NOT_FOUND.name())
                        .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), "Author with ID: " + id + " not found!")
                        .build());
    }

    private ResponseEntity<List<Author>> prepareResponseForGetAll(List<Author> authorList) {
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                .body(authorList);
    }
}


package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.catalog.application.port.AuthorUseCase;
import com.testaarosa.springRecallBookApp.catalog.domain.Author;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static com.testaarosa.springRecallBookApp.globalHeaderFactory.HttpHeaderFactory.getSuccessfulHeaders;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
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

        if (name.isPresent() && lastName.isPresent() && yearOfBirth.isPresent()) {
            List<Author> authors = authorUseCase.findAllByParams(AuthorQueryCommand.builder()
                    .name(name.get())
                    .lastName(lastName.get())
                    .yearOfBirth(yearOfBirth.get())
                    .limit(limit).build());
            return prepareResponseForGetAll(authors);
        }
        return prepareResponseForGetAll(authorUseCase.findAll());
    }

    private ResponseEntity<List<Author>> prepareResponseForGetAll(List<Author> authorList) {
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                .body(authorList);
    }
}


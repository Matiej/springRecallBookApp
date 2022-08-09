package com.testaarosa.spirngRecallBookApp.catalog.controller;

import com.testaarosa.spirngRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.testaarosa.spirngRecallBookApp.catalog.controller.HttpHeaderFactory.getSuccessfulHeaders;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogUseCase catalogUseCase;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author,
            @DefaultValue(value = "3") int limit) {
        if (title.isPresent() && author.isPresent()) {
            return prepareResponseForGetAll(catalogUseCase.findByTitleAndAuthor(title.get(), author.get()));
        } else if (title.isPresent()) {
            return prepareResponseForGetAll(catalogUseCase.findByTitle(title.get()));
        } else if (author.isPresent()) {
            return prepareResponseForGetAll(catalogUseCase.findByAuthor(author.get()));
        }
        return prepareResponseForGetAll(catalogUseCase.findAll()
                .stream()
                .limit(limit)
                .collect(Collectors.toList()));
    }

    private ResponseEntity<?> prepareResponseForGetAll(List<?> collection) {
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                .body(collection);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        return catalogUseCase.findById(id)
                .map(book -> ResponseEntity.ok()
                        .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                        .body(book))
                .orElse(ResponseEntity.notFound()
                        .headers(getSuccessfulHeaders(HttpStatus.NOT_FOUND, HttpMethod.GET))
                        .build());
    }
}

package com.testaarosa.spirngRecallBookApp.catalog.controller;

import com.testaarosa.spirngRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.testaarosa.spirngRecallBookApp.catalog.controller.HttpHeaderFactory.getSuccessfulHeaders;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogUseCase catalogUseCase;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAll() {
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                .body(catalogUseCase.findAll());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getBookById(@PathVariable Long id) {
        String xx = "";
        Optional<Book> optionalBook = catalogUseCase.findById(id);
        return optionalBook
                .<ResponseEntity<Object>>map(book -> ResponseEntity.ok()
                        .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                        .body(book))
                .orElseGet(() -> ResponseEntity.notFound().
                        headers(getSuccessfulHeaders(HttpStatus.NOT_FOUND, HttpMethod.GET)).build());


    }

}

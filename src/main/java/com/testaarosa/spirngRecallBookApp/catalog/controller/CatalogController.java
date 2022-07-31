package com.testaarosa.spirngRecallBookApp.catalog.controller;

import com.testaarosa.spirngRecallBookApp.catalog.application.port.CatalogUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.testaarosa.spirngRecallBookApp.catalog.controller.HttpHeaderFactory.getSuccessfulHeaders;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogUseCase catalogUseCase;

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAll() {
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpMethod.GET, HttpMethod.POST))
                .body(catalogUseCase.findAll());
    }

}

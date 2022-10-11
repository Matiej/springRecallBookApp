package com.testaarosa.springRecallBookApp.catalog.controller;


import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogInitializer;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.testaarosa.springRecallBookApp.globalHeaderFactory.HttpHeaderFactory.getSuccessfulHeaders;

@Slf4j
@RestController
@Secured(value = {"ROLE_ADMIN"})
@RequestMapping("/admin")
@AllArgsConstructor
@SecurityRequirement(name = "springrecallbook-api_documentation")
public class AdminController {
    private final CatalogInitializer catalogInitializer;

    @PostMapping("/initialization")
    public void initialize() {
        catalogInitializer.init();
    }

    @GetMapping("/check")
    public ResponseEntity<Object> check() {
        return ResponseEntity
                .status(200)
                .headers(getSuccessfulHeaders(HttpStatus.I_AM_A_TEAPOT, HttpMethod.GET))
                .body("ADMIN RESPONSE - OK");


    }
}

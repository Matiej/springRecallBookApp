package com.testaarosa.springRecallBookApp.catalog.controller;


import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogInitializer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
class AdminController {
    private final CatalogInitializer catalogInitializer;

    @GetMapping("/check")
    ResponseEntity<Object> check() {
        return ResponseEntity
                .status(200)
                .headers(getSuccessfulHeaders(HttpStatus.I_AM_A_TEAPOT, HttpMethod.GET))
                .body("ADMIN RESPONSE - OK");
    }

    @PostMapping("/books_initialize")
    void bookInitialize() {
        catalogInitializer.booksInit();
    }

    @PostMapping("/orders_initialize")
    @Operation(summary = "Add some orders",
            description = "Add orders, but first must add books")
    void orderInitialize() {
        catalogInitializer.ordersInit();
    }

    @PostMapping("/rolesinit")
    @Operation(summary = "Add default roles",
            description = "Add ADMIN and USER roles to data base")
    void rolesInit() {
        catalogInitializer.rolesInit();
    }
}

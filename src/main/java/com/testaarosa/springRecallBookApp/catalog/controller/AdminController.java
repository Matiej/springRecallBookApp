package com.testaarosa.springRecallBookApp.catalog.controller;


import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogInitializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private final CatalogInitializer catalogInitializer;

    @PostMapping("/initialization")
    public void initialize() {
        catalogInitializer.init();
    }
}

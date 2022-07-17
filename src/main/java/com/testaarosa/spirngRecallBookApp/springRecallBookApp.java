package com.testaarosa.spirngRecallBookApp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class springRecallBookApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(springRecallBookApp.class, args);
    }

    private final CatalogService catalogService;

    public springRecallBookApp(CatalogService catalogService) {
        this.catalogService = catalogService;
    }


    @Override
    public void run(String... args) throws Exception {
        List<Book> bookList = catalogService.findByTitle("Pan");
        System.out.println("Amount of books: " + bookList.size());
        bookList.forEach(book -> System.out.println("Books: " + book));
    }
}

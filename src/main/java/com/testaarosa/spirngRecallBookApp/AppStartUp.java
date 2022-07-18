package com.testaarosa.spirngRecallBookApp;

import com.testaarosa.spirngRecallBookApp.catalog.application.CatalogController;
import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppStartUp implements CommandLineRunner {
    private final CatalogController catalogController;

    public AppStartUp(CatalogController catalogController) {
        this.catalogController = catalogController;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Book> bookList = catalogController.findByTitle("Pan");
        System.out.println("Number of books found: " + bookList.size());
        bookList.forEach(book -> System.out.println("Books: " + book));
    }
}

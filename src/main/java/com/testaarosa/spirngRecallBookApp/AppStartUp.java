package com.testaarosa.spirngRecallBookApp;

import com.testaarosa.spirngRecallBookApp.catalog.application.CatalogController;
import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
//@RequiredArgsConstructor
public class AppStartUp implements CommandLineRunner {
    private final CatalogController catalogController;
    private final String schoolCatalogQuery;
    private final Long limit;

    public AppStartUp(CatalogController catalogController,
                      @Value("${recallBookApp.schoolCatalog.query}") String schoolCatalogQuery,
                      @Value("${recallBookApp.schoolCatalog.limit:5}") Long limit) {
        this.catalogController = catalogController;
        this.schoolCatalogQuery = schoolCatalogQuery;
        this.limit = limit;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Book> bookList = catalogController.findByTitle(schoolCatalogQuery)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
        System.out.println("Number of books found: " + bookList.size());
        bookList.forEach(book -> System.out.println("Books: " + book));
    }
}

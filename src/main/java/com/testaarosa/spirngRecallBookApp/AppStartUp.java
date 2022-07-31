package com.testaarosa.spirngRecallBookApp;

import com.testaarosa.spirngRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.CreateBookCommand;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.UpdateBookCommand;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.UpdateBookResponse;
import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import com.testaarosa.spirngRecallBookApp.order.application.port.PlaceOrderUseCase;
import com.testaarosa.spirngRecallBookApp.order.application.port.QueryOrderUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AppStartUp implements CommandLineRunner {
    private final CatalogUseCase catalogUseCase;
    private final PlaceOrderUseCase placeOrderUseCase;
    private final QueryOrderUseCase queryOrderUseCase;
    private final String catalogQuery;
    private final Long limit;

    public AppStartUp(CatalogUseCase catalogUseCase,
                      PlaceOrderUseCase placeOrderUseCase, QueryOrderUseCase queryOrderUseCase,
                      @Value("${recallBookApp.schoolCatalog.query}") String catalogQuery,
                      @Value("${recallBookApp.schoolCatalog.limit:5}") Long limit) {
        this.catalogUseCase = catalogUseCase;
        this.placeOrderUseCase = placeOrderUseCase;
        this.queryOrderUseCase = queryOrderUseCase;
        this.catalogQuery = catalogQuery;
        this.limit = limit;
    }

    @Override
    public void run(String... args) throws Exception {
        initData();
//        //findbyTitle();
//        findByTitleAndAuthor("black", "wienia");
//        findAndUpdate();
//        findByTitleAndAuthor("black", "wienia");
//        findAll();
        searchCatalog();
        searchOrder();
        placeAnOrder();
    }

    private void searchOrder() {
    }

    private void placeAnOrder() {

    }

    private void searchCatalog() {
        System.out.println("Find all books in storage..........");
        catalogUseCase.findAll().forEach(System.out::println);
    }

    private void initData() {
        catalogUseCase.addBook(new CreateBookCommand("Harry Potter", "Joanna Herwing ", 2022, new BigDecimal(112)));
        catalogUseCase.addBook(new CreateBookCommand("Black Out", "Wienia Karkowska", 2010, new BigDecimal(240)));
        catalogUseCase.addBook(new CreateBookCommand("Sezon Burz", "Stefan Burczymucha", 2005, new BigDecimal(281)));
        catalogUseCase.addBook(new CreateBookCommand("Black Knight", "Jowi Kielosi", 2015, new BigDecimal(110)));
    }

    private void findbyTitle() {
        List<Book> bookList = catalogUseCase.findByTitle(catalogQuery)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
        System.out.println("Number of books found: " + bookList.size());
        bookList.forEach(book -> System.out.println("Books: " + book));
    }

    private void findByTitleAndAuthor(String title, String author) {
        Optional<Book> oneByTitleAndAuthor = catalogUseCase.findOneByTitleAndAuthor(title, author);
        if (oneByTitleAndAuthor.isPresent()) {
            List<Book> foundBookList = oneByTitleAndAuthor
                    .stream()
                    .collect(Collectors.toList());
            System.out.println(foundBookList);
        } else {
            System.out.printf("No results for title: %s and author: %s", title, author);
        }
    }

    private void findAndUpdate() {
        System.out.println("Book updating....");
        catalogUseCase.findOneByTitleAndAuthor("black", "wienia")
                .ifPresent(book -> {
                    Long bookId = book.getId();
                    System.out.printf("Trying to update book ID: %d%n", bookId);
                    UpdateBookCommand bookCommand = UpdateBookCommand.builder(bookId)
                            .title("Total Black Out Disaster")
                            .build();
                    UpdateBookResponse updateBookResponse = catalogUseCase.updateBook(bookCommand);
                    System.out.printf("Book ID: %d updated with success: %s%n", bookId, updateBookResponse.isSuccess());
                });
    }
}

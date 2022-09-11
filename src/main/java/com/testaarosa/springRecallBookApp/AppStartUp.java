package com.testaarosa.springRecallBookApp;

import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.application.port.CreateBookCommand;
import com.testaarosa.springRecallBookApp.catalog.application.port.UpdateBookCommand;
import com.testaarosa.springRecallBookApp.catalog.application.port.UpdateBookResponse;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.order.application.port.OrderUseCase;
import com.testaarosa.springRecallBookApp.order.application.port.QueryOrderUseCase;
import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.recipient.application.port.SaveRecipientCommand;
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
    private final OrderUseCase orderUseCase;
    private final QueryOrderUseCase queryOrderUseCase;
    private final String catalogQuery;
    private final Long limit;
    private final RecipientUseCase recipientUseCase;

    public AppStartUp(CatalogUseCase catalogUseCase, OrderUseCase orderUseCase, QueryOrderUseCase queryOrderUseCase,
                      @Value("${recallBookApp.schoolCatalog.query}") String catalogQuery,
                      @Value("${recallBookApp.schoolCatalog.limit:5}") Long limit, RecipientUseCase recipientUseCase) {
        this.catalogUseCase = catalogUseCase;
        this.orderUseCase = orderUseCase;
        this.queryOrderUseCase = queryOrderUseCase;
        this.catalogQuery = catalogQuery;
        this.limit = limit;
        this.recipientUseCase = recipientUseCase;
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
        searchAnOrder();
        placeAnOrder();
    }

    private void searchAnOrder() {
    }

    private void placeAnOrder() {
//        System.out.println("trying to create an order....");
//        String black_out = "Black Out";
//        Book blackOutBook = catalogUseCase.findOneByTitle(black_out)
//                .orElseThrow(() -> new IllegalArgumentException(String.format("Cannot find a book: %s", black_out)));
//        String harry_potter = "Harry Potter";
//        Book harryPotterBook = catalogUseCase.findOneByTitle(harry_potter)
//                .orElseThrow(() -> new IllegalArgumentException(String.format("Cannot find a book: %s", harry_potter)));

        SaveRecipientCommand recipient = SaveRecipientCommand.builder()
                .name("Ksawery Nowak")
                .phone("661555777")
                .street("Starej Drogi 11")
                .city("Warszawa")
                .zipCode("01-001")
                .email("ksawer@gmail.com")
                .build();
        recipientUseCase.addRecipient(recipient);
        // create recipient

//        PlaceOrderCommand orderCommand = PlaceOrderCommand
//                .builder()
//                .recipient(recipient)
//                .item(new OrderItem(1, 11))
//                .item(new OrderItem(harryPotterBook.getId(), 7))
//                .build();
//        PlaceOrderResponse placeOrderResponse = placeOrderUseCase.placeOrder(orderCommand);
//        System.out.println("Created an order with ID: " + placeOrderResponse.getOrderId());

        queryOrderUseCase.findAll()
                .forEach(order -> System.out.printf("GOT Order with total price: " + order.totalPrice()
                        + "\n DETAILS: " + order));

    }

    private void searchCatalog() {
        System.out.println("Find all books in storage..........");
        catalogUseCase.findAll().forEach(System.out::println);
    }

    private void initData() {
        catalogUseCase.addBook(CreateBookCommand.builder().title("Harry Potter").author("Joanna Herwing").year(2022).price(new BigDecimal("112.00")).build());
        catalogUseCase.addBook(CreateBookCommand.builder().title("Black Out").author("Wienia Karkowska").year(2010).price(new BigDecimal("240.10")).build());
        catalogUseCase.addBook(CreateBookCommand.builder().title("Sezon Burz").author("Stefan Burczymucha").year(2005).price(new BigDecimal("281.01")).build());
        catalogUseCase.addBook(CreateBookCommand.builder().title("Black Knight").author("Jowi Kielosi").year(2014).price(new BigDecimal("90.89")).build());
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

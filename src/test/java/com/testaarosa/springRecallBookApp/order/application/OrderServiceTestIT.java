package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.catalog.dataBase.BookJpaRepository;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderServiceTestIT {
    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Autowired
    private OrderService orderService;

    @BeforeAll
    static void init(TestInfo testInfo) {
        log.info("Start test suite: {}.", testInfo.getTestClass());
    }

    @BeforeEach
    void setup(TestInfo testInfo) {
        log.info("Starting test: {}.", testInfo.getDisplayName());
    }

    @AfterAll
    static void afterAllTests(TestInfo testInfo) {
        log.info("Finished running the tests suit: {}." + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Should user can place order, method placeOrder()")
    void userCanPlaceOrder() {
        //given
        List<Book> books = prepareAdnAddBooks();
        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand.builder()
                .placeOrderRecipient(preparePlaceOrderRecipient())
                .item(new PlaceOrderItem(books.get(0).getId(), 11))
                .item(new PlaceOrderItem(books.get(1).getId(), 1))
                .build();

        //when
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);

        //then
        assertTrue(orderResponse.isSuccess());
    }

    private PlaceOrderRecipient preparePlaceOrderRecipient() {
        return PlaceOrderRecipient.builder()
                .name("Jan")
                .lastName("Nowak")
                .email("jannowa@nowa.pl")
                .build();
    }

    private List<Book> prepareAdnAddBooks() {

        Book effective_java= new Book(
                "Effective Java",
                2005,
                new BigDecimal(10),
                12L);

        Book mama_mia = new Book(
                "Mama mia",
                2015,
                new BigDecimal(10),
                12L);
        return bookJpaRepository.saveAll(Arrays.asList(effective_java, mama_mia));
    }
}
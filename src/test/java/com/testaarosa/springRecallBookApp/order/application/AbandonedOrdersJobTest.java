package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.catalog.dataBase.BookJpaRepository;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.clock.Clock;
import com.testaarosa.springRecallBookApp.order.OrderBaseTest;
import com.testaarosa.springRecallBookApp.order.dataBase.OrderJpaRepository;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.testaarosa.springRecallBookApp.order.domain.OrderStatus.ABANDONED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource("classpath:application-test.properties")
class AbandonedOrdersJobTest extends OrderBaseTest {

    @Autowired
    private AbandonedOrdersJob abandonedOrdersJob;
    @Autowired
    private BookJpaRepository bookJpaRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private Clock.Fake clock;

    @Test
    @Transactional
    @DisplayName("Should scheduled job AbandonedOrdersScheduledJobs run() change order status")
    @Disabled("Until not check other options to test scheduled jobs")
    void shouldSchedulerMarkOrdersAsAbandoned() throws InterruptedException {
        //given
        List<Book> books = prepareAndAddBooks();
        Book book1 = books.get(0);
        int book1OrderQuantity = 11;
        Long book1Available = book1.getAvailable();

        Book book2 = books.get(1);
        Long book2Available = book2.getAvailable();
        int book2OrderQuantity = 1;

        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, book1OrderQuantity, book2, book2OrderQuantity);
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);

        assertEquals(book1Available - book1OrderQuantity, book1.getAvailable());
        assertEquals(book2Available - book2OrderQuantity, book2.getAvailable());

        Thread.sleep(2_500);

        //when
        abandonedOrdersJob.run();

        //then
        Order order = orderJpaRepository.getReferenceById(orderResponse.getOrderId());
        assertEquals(ABANDONED, order.getOrderStatus());
        assertEquals(book1Available, book1.getAvailable());
        assertEquals(book2Available, book2.getAvailable());
    }

    @Test
    @Transactional
    @DisplayName("Should scheduled job AbandonedOrdersScheduledJobs with FAKE CLOCK run() change order status")
    void shouldSchedulerMarkOrdersAsAbandonedFakeClockTest() throws InterruptedException {
        //given
        List<Book> books = prepareAndAddBooks();
        Book book1 = books.get(0);
        int book1OrderQuantity = 11;
        Long book1Available = book1.getAvailable();

        Book book2 = books.get(1);
        Long book2Available = book2.getAvailable();
        int book2OrderQuantity = 1;

        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, book1OrderQuantity, book2, book2OrderQuantity);
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);

        assertEquals(book1Available - book1OrderQuantity, book1.getAvailable());
        assertEquals(book2Available - book2OrderQuantity, book2.getAvailable());

        clock.tick(Duration.of(35L, ChronoUnit.SECONDS));

        //when
        abandonedOrdersJob.run();

        //then
        Order order = orderJpaRepository.getReferenceById(orderResponse.getOrderId());
        assertEquals(ABANDONED, order.getOrderStatus());
        assertEquals(book1Available, book1.getAvailable());
        assertEquals(book2Available, book2.getAvailable());
    }

    private List<Book> prepareAndAddBooks() {
        return bookJpaRepository.saveAll(prepareBooks());
    }

    @TestConfiguration
    static class AbandonedOrdersJobTestConfiguration {
        @Bean
        public Clock.Fake clock() {
            return new Clock.Fake();
        }
    }
}
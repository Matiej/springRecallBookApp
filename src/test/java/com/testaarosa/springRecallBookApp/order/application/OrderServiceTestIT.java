package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.dataBase.BookJpaRepository;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.order.dataBase.OrderJpaRepository;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderItem;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.testaarosa.springRecallBookApp.order.domain.OrderStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderServiceTestIT {
    @Autowired
    private BookJpaRepository bookJpaRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private CatalogUseCase catalogUseCase;

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
    @DisplayName("Should user place an order, method placeOrder()")
    @Transactional
    void userCanPlaceOrder() {
        //given
        List<Book> books = prepareAndAddBooks();

        Book book1 = books.get(0);
        int book1OrderQuantity = 11;
        Long book1Available = book1.getAvailable();

        Book book2 = books.get(1);
        Long book2Available = book2.getAvailable();
        int book2OrderQuantity = 1;

        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, book1OrderQuantity, book2, book2OrderQuantity);

        //when
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);

        //then
        Book book1AfterOrder = catalogUseCase.findOne(book1.getId());
        Book book2AfterOrder = catalogUseCase.findOne(book2.getId());
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books",
                () -> assertEquals(book1AfterOrder.getAvailable(), book1Available - book1OrderQuantity),
                () -> assertEquals(book2AfterOrder.getAvailable(), book2Available - book2OrderQuantity),
                () -> assertNotEquals(book1AfterOrder.getAvailable() + 100, book1Available - book1OrderQuantity));

    }

    @Test
    @DisplayName("Should not place an order, not existing books, method placeOrder()")
    void userCannotPlaceOrderNotExistingBook() {
        //given
        int orderQuantity = 1;
        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand.builder()
                .placeOrderRecipient(preparePlaceOrderRecipient())
                .item(new PlaceOrderItem(111L, orderQuantity))
                .build();
        String expectedErrorMessage = "Unable to find com.testaarosa.springRecallBookApp.catalog.domain.Book with id " + 111;


        //when
        Throwable throwable = catchThrowable(() -> orderService.placeOrder(placeOrderCommand));

        //then
        then(throwable).as("An EntityNotFoundException should be thrown if more books as available")
                .isInstanceOf(EntityNotFoundException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @DisplayName("Should not place an order, not enough available books, method placeOrder()")
    void userCannotPlaceOrder() {
        //given
        List<Book> books = prepareAndAddBooks();
        int orderQuantity = 100;
        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(books.get(0), orderQuantity, books.get(1), 1);
        PlaceOrderItem placeOrderItem = placeOrderCommand.getItemList().get(0);
        String expectedErrorMessage = "Too many books id: " + placeOrderItem.getBookId() + " requested: "
                + orderQuantity + " of: " + books.get(0).getAvailable() + " available";


        //when
        Throwable throwable = catchThrowable(() -> orderService.placeOrder(placeOrderCommand));

        //then
        then(throwable).as("An IllegalArgumentException should be thrown if more books as available")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @Transactional
    @DisplayName("Should remove an order, method removeOrderById(), verify book have revoked.")
    void userCanRemoveOrderVerifyAvailable() {
        //given
        List<Book> books = prepareAndAddBooks();
        Book book1 = books.get(0);
        Long book1Available = book1.getAvailable();
        Book book2 = books.get(1);
        Long book2Available = book2.getAvailable();
        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, 10, book2, 2);
        //then
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        Book book1AfterOrder = catalogUseCase.findOne(book1.getId());
        Book book2AfterOrder = catalogUseCase.findOne(book2.getId());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1AfterOrder.getAvailable(), book1Available - 10),
                () -> assertEquals(book2AfterOrder.getAvailable(), book2Available - 2));

        //when
        orderService.removeOrderById(orderResponse.getOrderId());

        //then
        assertAll("Check availability for given books after remove anorder",
                () -> assertEquals(book1AfterOrder.getAvailable(), book1Available),
                () -> assertEquals(book2AfterOrder.getAvailable(), book2Available),
                () -> assertNotEquals(book2AfterOrder.getAvailable(), book2Available - 2));
    }

    @Test
    @DisplayName("Should not remove not existing order, method removeOrderById() throws exception")
    void userCantRemoveOrderThrowsException() {
        //given
        List<Book> books = prepareAndAddBooks();
        Book book1 = books.get(0);
        Book book2 = books.get(1);
        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, 10, book2, 2);
        //then
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());

        Long fakeOrderId = orderResponse.getOrderId() + 15;
        String expectedErrorMessage = "Cannot find order ID: " + fakeOrderId;

        //when
        Throwable throwable = catchThrowable(() -> orderService.removeOrderById(fakeOrderId));

        //then
        then(throwable).as("An IllegalArgumentException should be thrown if more books as available")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @Transactional
    @DisplayName("Should change order status to CANCELED, method updateOrderStatus(), verify book have revoked.")
    void shouldChangeOrderStatusAndVerifyBookAvailable() {
        //given
        List<Book> books = prepareAndAddBooks();

        Book book1 = books.get(0);
        int book1OrderQty = 10;
        Long book1Available = book1.getAvailable();

        Book book2 = books.get(1);
        int book2OrderQty = 2;
        Long book2Available = book2.getAvailable();

        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, book1OrderQty, book2, book2OrderQty);
        //then
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1.getAvailable(), book1Available - book1OrderQty),
                () -> assertEquals(book2.getAvailable(), book2Available - book2OrderQty));

        //when
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand(orderResponse.getOrderId(), CANCELED,
                placeOrderCommand.getPlaceOrderRecipient().getEmail());
        OrderResponse canceledOrderResponse = orderService.updateOrderStatus(command);

        //then
        Order order = orderJpaRepository.getReferenceById(canceledOrderResponse.getOrderId());
        assertEquals(order.getOrderStatus(), CANCELED);
        assertAll("Check availability for given books after remove an order",
                () -> assertEquals(book1.getAvailable(), book1Available),
                () -> assertEquals(book2.getAvailable(), book2Available),
                () -> assertNotEquals(book2.getAvailable(), book2Available - 2));
    }


    @Test
    @Transactional
    @DisplayName("Should change order status to PAID, method updateOrderStatus(), verify book that have NOT revoked")
    void shouldChangeOrderStatusAndVerifyBookAvailableNotRevoke() {
        //given
        List<Book> books = prepareAndAddBooks();

        Book book1 = books.get(0);
        int book1OrderQty = 10;
        Long book1Available = book1.getAvailable();

        Book book2 = books.get(1);
        int book2OrderQty = 2;
        Long book2Available = book2.getAvailable();

        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, book1OrderQty, book2, book2OrderQty);
        //then
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1.getAvailable(), book1Available - book1OrderQty),
                () -> assertEquals(book2.getAvailable(), book2Available - book2OrderQty));

        //when
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand(orderResponse.getOrderId(), PAID,
                placeOrderCommand.getPlaceOrderRecipient().getEmail());
        orderService.updateOrderStatus(command);

        //then
        assertAll("Check availability for given books after remove anorder",
                () -> assertEquals(book1.getAvailable(), book1Available - book1OrderQty),
                () -> assertEquals(book2.getAvailable(), book2Available - book2OrderQty),
                () -> assertNotEquals(book2.getAvailable(), book2Available + 12));
    }

    @Test
    @DisplayName("Should change order status from PAID to Canceled, method updateOrderStatus()")
    void shouldNotChangePaidOrderStatusToCanceledThrowsException() {
        //given
        List<Book> books = prepareAndAddBooks();
        Book book1 = books.get(0);
        Book book2 = books.get(1);
        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, 1, book2, 1);
        String expectedErrorMessage = "Unable to change current order status: " + PAID.name()
                + "  to '" + CANCELED.name() + "' status.";
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());

        orderService.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(), PAID, placeOrderCommand.getPlaceOrderRecipient().getEmail()));

        //when
        Throwable throwable = catchThrowable(
                () -> orderService.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(), CANCELED, placeOrderCommand.getPlaceOrderRecipient().getEmail())));

        //then
        then(throwable).as("An IllegalArgumentException should be thrown if more books as available")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @DisplayName("Should not change order status from SHIPPED to Canceled, method updateOrderStatus()")
    void shouldNotChangeSHIPPEDOrderStatusToCanceledThrowException() {
        //given
        List<Book> books = prepareAndAddBooks();
        Book book1 = books.get(0);
        Book book2 = books.get(1);
        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, 1, book2, 1);
        String expectedErrorMessage = "Unable to change current order status: " + SHIPPED.name()
                + "  to '" + CANCELED.name() + "' status.";
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        orderService.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(), PAID, placeOrderCommand.getPlaceOrderRecipient().getEmail()));
        orderService.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(), SHIPPED, placeOrderCommand.getPlaceOrderRecipient().getEmail()));

        //when
        Throwable throwable = catchThrowable(() -> orderService.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(),
                CANCELED,
                placeOrderCommand.getPlaceOrderRecipient().getEmail())));

        //then
        then(throwable).as("An IllegalArgumentException should be thrown if more books as available")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @Transactional
    @DisplayName("should not revoke other users order")
    public void userCannotRevokeOtherUsersOrder() {
        //given
        List<Book> books = prepareAndAddBooks();
        Book book1 = books.get(0);
        Long availableBeforeOrder = book1.getAvailable();

        PlaceOrderRecipient placeOrderRecipient = preparePlaceOrderRecipient();
        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, 1, books.get(1), 2,
                placeOrderRecipient);
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);
        assertEquals(availableBeforeOrder - 1, book1.getAvailable());

        //when
        String updateRecipientEmail = "alienRecipient@alien.com";
        orderService.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(), CANCELED,
                updateRecipientEmail));

        //then
        Order order = orderJpaRepository.getReferenceById(orderResponse.getOrderId());
        assertEquals(NEW, order.getOrderStatus());
        assertEquals(availableBeforeOrder - 1, book1.getAvailable());

    }

    @Test
    @Transactional
    @DisplayName("Should update order items, method updateOrderItem(), verify book that have revoked")
    void shouldUpdateOrderItems() {
        //given
        List<Book> books = prepareAndAddBooks();

        Book book1 = books.get(0);
        int book1OrderQty = 10;
        Long book1Available = book1.getAvailable();

        Book book2 = books.get(1);
        int book2OrderQty = 2;
        Long book2Available = book2.getAvailable();

        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, book1OrderQty, book2, book2OrderQty);

        //then
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1Available - book1OrderQty, book1.getAvailable()),
                () -> assertEquals(book2Available - book2OrderQty, book2.getAvailable()));

        //when
        OrderResponse response = orderService.updateOrderItems(UpdateOrderItemsCommand.builder()
                .orderId(orderResponse.getOrderId())
                .item(placeOrderCommand.getItemList().get(1))
                        .recipientEmail("superadmin@admin.org")
                .build());

        //then
        assertNotNull(response);
        Order order = orderJpaRepository.getReferenceById(orderResponse.getOrderId());
        Set<OrderItem> itemList = order.getItemList();
        assertAll("Check order items",
                () -> assertNotNull(itemList),
                () -> assertEquals(1, itemList.size()));
        assertAll("Check availability for given books after remove anorder",
                () -> assertEquals(book1.getAvailable(), book1Available),
                () -> assertEquals(book2.getAvailable(), book2Available - book2OrderQty),
                () -> assertNotEquals(book2.getAvailable(), book2Available + 12));
    }

    @Test
    @Transactional
    @DisplayName("Should NOT update order items, not authorized, method updateOrderItem(), verify book that have revoked")
    void shouldNotUpdateOrderItemsOtherUserOrder() {
        //given
        List<Book> books = prepareAndAddBooks();

        Book book1 = books.get(0);
        int book1OrderQty = 10;
        Long book1Available = book1.getAvailable();

        Book book2 = books.get(1);
        int book2OrderQty = 2;
        Long book2Available = book2.getAvailable();

        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, book1OrderQty, book2, book2OrderQty);

        //then
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1Available - book1OrderQty, book1.getAvailable()),
                () -> assertEquals(book2Available - book2OrderQty, book2.getAvailable()));

        //when
        OrderResponse response = orderService.updateOrderItems(UpdateOrderItemsCommand.builder()
                .orderId(orderResponse.getOrderId())
                .recipientEmail("alienRecipient@notauthorized.com")
                .item(placeOrderCommand.getItemList().get(1))
                .build());

        //then
        assertNotNull(response);
        Order order = orderJpaRepository.getReferenceById(orderResponse.getOrderId());
        Set<OrderItem> itemList = order.getItemList();
        assertAll("Check order items",
                () -> assertNotNull(itemList),
                () -> assertEquals(2, itemList.size()));
        assertAll("Check availability for given books after remove an order",
                () -> assertEquals(book1Available - book1OrderQty, book1.getAvailable()),
                () -> assertEquals(book2Available - book2OrderQty, book2.getAvailable()),
                () -> assertNotEquals(book2.getAvailable(), book2Available + 12));
    }

    @Test
    @Transactional
    @DisplayName("Should NOT update order items, method updateOrderItem(), not existing order throws exception.")
    void shouldNotUpdateOrderItemsForNotExistingOrderThrowsException() {
        //given
        String expectedErrorMessage = "Can't find order with id: 111";
        List<Book> books = prepareAndAddBooks();

        Book book1 = books.get(0);
        int book1OrderQty = 10;
        Long book1Available = book1.getAvailable();

        Book book2 = books.get(1);
        int book2OrderQty = 2;
        Long book2Available = book2.getAvailable();

        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, book1OrderQty, book2, book2OrderQty);

        //then
        OrderResponse orderResponse = orderService.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1Available - book1OrderQty, book1.getAvailable()),
                () -> assertEquals(book2Available - book2OrderQty, book2.getAvailable()));

        //when
        Throwable throwable = catchThrowable(() -> orderService.updateOrderItems(UpdateOrderItemsCommand.builder()
                .orderId(111L)
                .item(placeOrderCommand.getItemList().get(1))
                .build()));

        //then
        then(throwable).as("An IllegalArgumentException should be thrown if more books as available")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    private UpdateOrderStatusCommand getUpdateOrderStatusCommand(Long orderResponse, OrderStatus orderStatus,
                                                                 String recipientEmail) {
        return UpdateOrderStatusCommand
                .builder()
                .orderId(orderResponse)
                .orderStatus(orderStatus)
                .recipientEmail(recipientEmail)
                .build();
    }

    private PlaceOrderCommand getPlaceOrderCommand(Book book1, int quantity1, Book book2, int quantity2, PlaceOrderRecipient recipient) {
        return PlaceOrderCommand.builder()
                .placeOrderRecipient(preparePlaceOrderRecipient())
                .item(new PlaceOrderItem(book1.getId(), quantity1))
                .item(new PlaceOrderItem(book2.getId(), quantity2))
                .build();
    }

    private PlaceOrderCommand getPlaceOrderCommand(Book book1, int quantity1, Book book2, int quantity2) {
        return getPlaceOrderCommand(book1, quantity1, book2, quantity2, preparePlaceOrderRecipient());
    }

    private PlaceOrderRecipient preparePlaceOrderRecipient() {
        return preparePlaceOrderRecipient("jannowa@nowa.pl");
    }

    private PlaceOrderRecipient preparePlaceOrderRecipient(String email) {
        return PlaceOrderRecipient.builder()
                .name("Jan")
                .lastName("Nowak")
                .email(email)
                .build();
    }

    private List<Book> prepareAndAddBooks() {
        return bookJpaRepository.saveAll(prepareBooks());
    }

    private List<Book> prepareBooks() {
        Book effective_java = new Book(
                "Effective Java",
                2005,
                new BigDecimal(10),
                12L);

        Book mama_mia = new Book(
                "Mama mia",
                2015,
                new BigDecimal(10),
                12L);
        return Arrays.asList(effective_java, mama_mia);
    }
}
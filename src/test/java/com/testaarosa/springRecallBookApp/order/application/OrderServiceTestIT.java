package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.dataBase.BookJpaRepository;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.order.OrderBaseTest;
import com.testaarosa.springRecallBookApp.order.application.port.OrderUseCase;
import com.testaarosa.springRecallBookApp.order.application.port.QueryOrderUseCase;
import com.testaarosa.springRecallBookApp.order.dataBase.OrderJpaRepository;
import com.testaarosa.springRecallBookApp.order.domain.Delivery;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderItem;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
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
@TestPropertySource("classpath:application-test.properties")
class OrderServiceTestIT extends OrderBaseTest {
    @Autowired
    private BookJpaRepository bookJpaRepository;
    @Autowired
    private OrderUseCase orderUseCase;
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private CatalogUseCase catalogUseCase;
    @Autowired
    private QueryOrderUseCase queryOrderUseCase;

    @BeforeEach
    void setup(TestInfo testInfo) {
        log.info("Starting test: {}.", testInfo.getDisplayName());
    }

    @AfterEach
    void tier(TestInfo testInfo) {
        log.info("Finished test:  {}", testInfo.getDisplayName());
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
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);

        //then
        Order order = orderJpaRepository.getReferenceById(orderResponse.getOrderId());
        Book book1AfterOrder = catalogUseCase.findOne(book1.getId());
        Book book2AfterOrder = catalogUseCase.findOne(book2.getId());
        assertTrue(orderResponse.isSuccess());
        assertEquals(NEW, order.getOrderStatus());
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
        Throwable throwable = catchThrowable(() -> orderUseCase.placeOrder(placeOrderCommand));

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
        Throwable throwable = catchThrowable(() -> orderUseCase.placeOrder(placeOrderCommand));

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
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        Book book1AfterOrder = catalogUseCase.findOne(book1.getId());
        Book book2AfterOrder = catalogUseCase.findOne(book2.getId());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1AfterOrder.getAvailable(), book1Available - 10),
                () -> assertEquals(book2AfterOrder.getAvailable(), book2Available - 2));

        //when
        orderUseCase.removeOrderById(orderResponse.getOrderId());

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
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());

        Long fakeOrderId = orderResponse.getOrderId() + 15;
        String expectedErrorMessage = "Cannot find order ID: " + fakeOrderId;

        //when
        Throwable throwable = catchThrowable(() -> orderUseCase.removeOrderById(fakeOrderId));

        //then
        then(throwable).as("An IllegalArgumentException should be thrown if more books as available")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @Transactional
    @DisplayName("Should change order status to CANCELED, method updateOrderStatus(), verify book has revoked.")
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
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1.getAvailable(), book1Available - book1OrderQty),
                () -> assertEquals(book2.getAvailable(), book2Available - book2OrderQty));

        //when
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand(orderResponse.getOrderId(), CANCELED,
                user(placeOrderCommand.getPlaceOrderRecipient().getEmail()));
        OrderResponse canceledOrderResponse = orderUseCase.updateOrderStatus(command);

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
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1.getAvailable(), book1Available - book1OrderQty),
                () -> assertEquals(book2.getAvailable(), book2Available - book2OrderQty));

        //when
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand(orderResponse.getOrderId(), PAID,
                user(placeOrderCommand.getPlaceOrderRecipient().getEmail()));
        orderUseCase.updateOrderStatus(command);

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
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());

        orderUseCase.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(), PAID,
                user(placeOrderCommand.getPlaceOrderRecipient().getEmail())));

        //when
        Throwable throwable = catchThrowable(
                () -> orderUseCase.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(), CANCELED,
                        user(placeOrderCommand.getPlaceOrderRecipient().getEmail()))));

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
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        orderUseCase.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(), PAID,
                user(placeOrderCommand.getPlaceOrderRecipient().getEmail())));
        orderUseCase.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(), SHIPPED,
                user(placeOrderCommand.getPlaceOrderRecipient().getEmail())));

        //when
        Throwable throwable = catchThrowable(() -> orderUseCase.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(),
                CANCELED, user(placeOrderCommand.getPlaceOrderRecipient().getEmail()))));

        //then
        then(throwable).as("An IllegalArgumentException should be thrown if more books as available")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @Transactional
    @DisplayName("Should not revoke other users order, method updateOrderStatus()")
    public void shouldNotUpdateOtherUsersOrder() {
        //given
        List<Book> books = prepareAndAddBooks();
        Book book1 = books.get(0);
        Long availableBeforeOrder = book1.getAvailable();

        PlaceOrderRecipient placeOrderRecipient = preparePlaceOrderRecipient();
        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, 1, books.get(1), 2,
                placeOrderRecipient);
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertEquals(availableBeforeOrder - 1, book1.getAvailable());

        //when
        String updateRecipientEmail = "alienRecipient@alien.com";
        orderUseCase.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(), CANCELED,
                user(updateRecipientEmail)));

        //then
        Order order = orderJpaRepository.getReferenceById(orderResponse.getOrderId());
        assertEquals(NEW, order.getOrderStatus());
        assertEquals(availableBeforeOrder - 1, book1.getAvailable());
    }

    @Test
    @Transactional
    @DisplayName("Should admin update other users order, method updateOrderStatus()")
    public void shouldAdminUpdateOtherUsersOrder() {
        //given
        List<Book> books = prepareAndAddBooks();
        Book book1 = books.get(0);
        Long availableBeforeOrder = book1.getAvailable();

        PlaceOrderRecipient placeOrderRecipient = preparePlaceOrderRecipient();
        PlaceOrderCommand placeOrderCommand = getPlaceOrderCommand(book1, 1, books.get(1), 2,
                placeOrderRecipient);
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertEquals(availableBeforeOrder - 1, book1.getAvailable());

        //when
        orderUseCase.updateOrderStatus(getUpdateOrderStatusCommand(orderResponse.getOrderId(), CANCELED,
                admin()));

        //then
        Order order = orderJpaRepository.getReferenceById(orderResponse.getOrderId());
        assertEquals(CANCELED, order.getOrderStatus());
        assertEquals(availableBeforeOrder, book1.getAvailable());
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
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1Available - book1OrderQty, book1.getAvailable()),
                () -> assertEquals(book2Available - book2OrderQty, book2.getAvailable()));

        //when
        OrderResponse response = orderUseCase.updateOrderItems(UpdateOrderItemsCommand.builder()
                .orderId(orderResponse.getOrderId())
                .item(placeOrderCommand.getItemList().get(1))
                .user(user(preparePlaceOrderRecipient().getEmail()))
                .build());

        //then
        assertNotNull(response);
        Order order = orderJpaRepository.getReferenceById(orderResponse.getOrderId());
        Set<OrderItem> itemList = order.getOrderItems();
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
    @DisplayName("Should ADMIN update other user order items, method updateOrderItem(), verify book that have revoked")
    void shouldAdminUpdateOtherUserOrderItems() {
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
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1Available - book1OrderQty, book1.getAvailable()),
                () -> assertEquals(book2Available - book2OrderQty, book2.getAvailable()));

        //when
        OrderResponse response = orderUseCase.updateOrderItems(UpdateOrderItemsCommand.builder()
                .orderId(orderResponse.getOrderId())
                .item(placeOrderCommand.getItemList().get(1))
                .user(admin())
                .build());

        //then
        assertNotNull(response);
        Order order = orderJpaRepository.getReferenceById(orderResponse.getOrderId());
        Set<OrderItem> itemList = order.getOrderItems();
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

        String unauthorizedUser = "alienRecipient@notauthorized.com";
        //then
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1Available - book1OrderQty, book1.getAvailable()),
                () -> assertEquals(book2Available - book2OrderQty, book2.getAvailable()));

        //when
        OrderResponse response = orderUseCase.updateOrderItems(UpdateOrderItemsCommand.builder()
                .orderId(orderResponse.getOrderId())
                .user(user(unauthorizedUser))
                .item(placeOrderCommand.getItemList().get(1))
                .build());

        //then
        assertNotNull(response);
        Order order = orderJpaRepository.getReferenceById(orderResponse.getOrderId());
        Set<OrderItem> itemList = order.getOrderItems();
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
        OrderResponse orderResponse = orderUseCase.placeOrder(placeOrderCommand);
        assertTrue(orderResponse.isSuccess());
        assertAll("Check availability for given books before remove an order",
                () -> assertEquals(book1Available - book1OrderQty, book1.getAvailable()),
                () -> assertEquals(book2Available - book2OrderQty, book2.getAvailable()));

        //when
        Throwable throwable = catchThrowable(() -> orderUseCase.updateOrderItems(UpdateOrderItemsCommand.builder()
                .orderId(111L)
                .item(placeOrderCommand.getItemList().get(1))
                .build()));

        //then
        then(throwable).as("An IllegalArgumentException should be thrown if more books as available")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @DisplayName("Should be shipping cost added to total order price, method placeOrder().")
    void shouldShippingCostAddedToTotalOrderPrice() {
        //given
        Book book = prepareOneBook(20, "39.99");

        //when
        PlaceOrderCommand command = getPlaceOrderCommand(book, 1, Delivery.COURIER);
        OrderResponse orderResponse = orderUseCase.placeOrder(command);

        //then
        assertEquals("49.89", queryOrderUseCase.findOrderById(orderResponse.getOrderId())
                .get().getFinalPrice().toPlainString());

    }

    @Test
    @DisplayName("Should be shipping cost discounted if order value over 100, method placeOrder().")
    void shouldShippingCostDiscountedOver100Value() {
        //given
        Book book = prepareOneBook(20, "39.99");

        //when
        PlaceOrderCommand command = getPlaceOrderCommand(book, 3, Delivery.COURIER);
        OrderResponse orderResponse = orderUseCase.placeOrder(command);

        //then
        RichOrder richOrder = queryOrderUseCase.findOrderById(orderResponse.getOrderId()).get();
        assertEquals("119.97", richOrder.getFinalPrice().toPlainString());
        assertEquals("119.97", richOrder.getOrderPrice().getItemsPrice().toPlainString());
    }

    @Test
    @DisplayName("Should cheapest book be for half price when total over 200, method placeOrder().")
    void shouldCheapestBookIsHalfPriceWhenTotalOver200Value() {
        Book book = prepareOneBook(20, "39.99");

        //when
        PlaceOrderCommand command = getPlaceOrderCommand(book, 6, Delivery.COURIER);
        OrderResponse orderResponse = orderUseCase.placeOrder(command);

        //then
        RichOrder richOrder = queryOrderUseCase.findOrderById(orderResponse.getOrderId()).get();
        assertEquals("219.94", richOrder.getFinalPrice().toPlainString());
    }

    @Test
    @DisplayName("Should cheapest book be for free when total over 400, method placeOrder().")
    void shouldCheapestBookIsFreeWhenTotalOver400Value() {
        Book book = prepareOneBook(20, "41.20");

        //when
        PlaceOrderCommand command = getPlaceOrderCommand(book, 10, Delivery.COURIER);
        OrderResponse orderResponse = orderUseCase.placeOrder(command);

        //then
        RichOrder richOrder = queryOrderUseCase.findOrderById(orderResponse.getOrderId()).get();
        assertEquals("370.80", richOrder.getFinalPrice().toPlainString());
    }

    private List<Book> prepareAndAddBooks() {
        return bookJpaRepository.saveAll(prepareBooks());
    }

    private Book prepareOneBook(long available, String price) {
        return bookJpaRepository.save(new Book("Mama mia", 2015, new BigDecimal(price), available));
    }

    private UpdateOrderStatusCommand getUpdateOrderStatusCommand(Long orderResponse, OrderStatus orderStatus,
                                                                 User user) {
        return UpdateOrderStatusCommand
                .builder()
                .orderId(orderResponse)
                .orderStatus(orderStatus)
                .user(user)
                .build();
    }

}
package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.order.application.port.*;
import com.testaarosa.springRecallBookApp.order.dataBase.OrderJpaRepository;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderItem;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService implements OrderUseCase {
    private final OrderJpaRepository repository;
    private final RecipientUseCase recipientUseCase;
    private final CatalogUseCase catalogUseCase;

    @Override
    @Transactional
    public OrderResponse placeOrder(PlaceOrderCommand command) {
        Set<OrderItem> orderItemList = getOrderItems(command.getItemList());

        Recipient orderRecipient = command.getPlaceOrderRecipient().toRecipient();

        Recipient recipient = recipientUseCase.findOneByEmail(command.getPlaceOrderRecipient().getEmail())
                .map(foundRecipient -> {
                    foundRecipient.updateFields(orderRecipient);
                    recipientUseCase.updateRecipient(foundRecipient);
                    return foundRecipient;
                })
                .orElse(orderRecipient);

        Order order = Order.builder()
                .orderStatus(OrderStatus.NEW)
                .recipient(recipient)
                .itemList(orderItemList)
                .build();
        recipient.addOrder(order);
        Order savedOrder = repository.save(order);
        catalogUseCase.saveAll(updateBooksQuantity(orderItemList));
        return OrderResponse.success(savedOrder.getId());
    }

    private Set<Book> updateBooksQuantity(Set<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() - item.getQuantity());
                    return book;
                }).collect(Collectors.toSet());
    }

    @Override
    public OrderResponse updateOrder(UpdateOrderCommand command) {

        Set<OrderItem> orderItemList = getOrderItems(command.getItemList());
        Order order = repository.findById(command.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Can't find order with id: " + command.getOrderId()));

        order.replaceOrderItems(orderItemList);
        order.setLastUpdatedAt(LocalDateTime.now());
        Order savedOrder = repository.save(order);
        return OrderResponse.success(savedOrder.getId());
    }

    @Override
    public void removeOrderById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public OrderResponse updateOrderStatus(Long id, String orderStatus) {
        OrderResponse.OrderResponseBuilder orderResponseBuilder = OrderResponse.builder();
        OrderStatus.parseOrderString(orderStatus).ifPresentOrElse(status -> repository.findById(id)
                .ifPresentOrElse(order -> {
                            order.updateOrderStatus(status);
                            repository.save(order);
                            orderResponseBuilder
                                    .orderId(id)
                                    .success(true);
                        },
                        () -> orderResponseBuilder
                                .success(false)
                                .errorList(List.of("Can't find order with ID: " + id))), () -> orderResponseBuilder
                .success(false)
                .errorList(List.of("Unable to find given order status: '" + orderStatus + "'.")));
        return orderResponseBuilder.build();
    }

    private Set<OrderItem> getOrderItems(List<PlaceOrderItem> placeOrderItems) {
        return placeOrderItems
                .stream()
                .map(this::toOrderItem)
                .collect(Collectors.toSet());
    }

    private OrderItem toOrderItem(PlaceOrderItem placeOrderItem) {
        Book book = catalogUseCase.findOne(placeOrderItem.getBookId());
        int placeOrderItemQuantity = placeOrderItem.getQuantity();
        Long bookAvailable = book.getAvailable();
        if (bookAvailable >= placeOrderItemQuantity) {
            return OrderItem.builder()
                    .book(book)
                    .quantity(placeOrderItemQuantity)
                    .build();
        }
        throw new IllegalArgumentException("Too many books id: " + placeOrderItem.getBookId() +
                " requested: " + placeOrderItemQuantity + " of: " + bookAvailable + " available");
    }

}
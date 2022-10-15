package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.order.application.port.OrderUseCase;
import com.testaarosa.springRecallBookApp.order.dataBase.OrderJpaRepository;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderItem;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import com.testaarosa.springRecallBookApp.order.domain.UpdateOrderStatusResult;
import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.security.UserSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class OrderService implements OrderUseCase {
    private final OrderJpaRepository repository;
    private final RecipientUseCase recipientUseCase;
    private final CatalogUseCase catalogUseCase;
    private final UserSecurity userSecurity;

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
                .recipient(recipient)
                .orderItems(orderItemList)
                .delivery(command.getDelivery())
                .build();
        recipient.addOrder(order);
        Order savedOrder = repository.save(order);
        catalogUseCase.saveAll(reduceBooksQuantity(orderItemList));
        return OrderResponse.success(savedOrder.getId());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderItems(UpdateOrderItemsCommand command) {
        Set<OrderItem> orderItemList = getOrderItems(command.getItemList());
        Order order = repository.findById(command.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Can't find order with id: " + command.getOrderId()));

        if (!userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(), command.getUser())) {
            String errorMessage = "Unauthorized action for user: " + command.getUser().getUsername();
            return OrderResponse.failure(order.getId(), errorMessage, OrderResponse.OrderErrorStatus.FORBIDDEN);
        }
        revokeBooksQuantity(order.getOrderItems());
        order.replaceOrderItems(orderItemList);
        reduceBooksQuantity(orderItemList);
        order.setLastUpdatedAt(LocalDateTime.now());
        Order savedOrder = repository.save(order);
        return OrderResponse.success(savedOrder.getId());
    }

    private Set<Book> reduceBooksQuantity(Set<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() - item.getQuantity());
                    return book;
                }).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UpdateOrderStatusCommand command) {
        Long orderId = command.getOrderId();
        String orderStatus = command.getOrderStatus().name();
        return repository.findById(orderId)
                .map(order -> {
                    if (!userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(),command.getUser())) {
                        String errorMessage = "Unauthorized action for user: " + command.getUser().getUsername();
                        return OrderResponse.failure(order.getId(), errorMessage, OrderResponse.OrderErrorStatus.FORBIDDEN);
                    }
                    Optional<OrderStatus> statusOptional = OrderStatus.parseOrderString(orderStatus);
                    if (statusOptional.isEmpty()) {
                        String errorMessage = "Unable to find given order status: '" + orderStatus + "'.";
                        return OrderResponse.failure(order.getId(), errorMessage, OrderResponse.OrderErrorStatus.NOT_FOUND);
                    }
                    UpdateOrderStatusResult result = order.updateOrderStatus(statusOptional.get());
                    if (result.isRevoked()) {
                        revokeBooksQuantity(order.getOrderItems());
                    }
                    return OrderResponse.success(orderId);
                })
                .orElseThrow(() -> new IllegalArgumentException("Can't find order with id: " + command.getOrderId()));
    }

    @Override
    @Transactional
    public void removeOrderById(Long id) {
        repository.findById(id).ifPresentOrElse(order -> {
                    revokeBooksQuantity(order.getOrderItems());
                    repository.deleteById(id);
                },
                () -> {
                    throw new IllegalArgumentException("Cannot find order ID: " + id);
                });
    }

    private Set<Book> revokeBooksQuantity(Set<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() + item.getQuantity());
                    return book;
                }).collect(Collectors.toSet());
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
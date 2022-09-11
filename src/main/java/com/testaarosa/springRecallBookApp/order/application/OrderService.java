package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.application.port.PlaceOrderItem;
import com.testaarosa.springRecallBookApp.order.application.port.*;
import com.testaarosa.springRecallBookApp.order.domain.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService implements OrderUseCase {
    private final OrderRepository repository;
    private final RecipientUseCase recipientUseCase;
    private final CatalogUseCase catalogUseCase;

    @Override
    public OrderResponse placeOrder(PlaceOrderCommand command) {
        List<String> errorList = new ArrayList<>();

        List<OrderItem> orderItemList = getOrderItemList(command.getItemList());

        Optional<Recipient> optionalRecipient = recipientUseCase.findById(command.getRecipientId());

        if (orderItemList.isEmpty()) {
            errorList.add("No books found for Id's: " + command.getItemList().stream().map(item -> String.valueOf(item.getBookId())).collect(Collectors.joining(", ")));
        }
        if (optionalRecipient.isEmpty()) {
            errorList.add("No recipient found for Id:" + command.getRecipientId());
        }
        if (errorList.isEmpty()) {
            Recipient recipient = optionalRecipient.get();
            Order order = Order.builder()
                    .orderStatus(OrderStatus.NEW)
                    .recipient(recipient)
                    .itemList(orderItemList)
                    .createdAt(LocalDateTime.now())
                    .build();
            Order savedOrder = repository.save(order);
            return OrderResponse.success(savedOrder.getId());
        }
        return OrderResponse.failure(errorList);
    }

    @Override
    public OrderResponse updateOrder(UpdateOrderCommand command) {
        List<String> errorList = new ArrayList<>();

        List<OrderItem> orderItemList = getOrderItemList(command.getItemList());
        Optional<Order> optionalOrder = repository.findOrderById(command.getOrderId());

        if (orderItemList.isEmpty()) {
            errorList.add("No books found for Id's: " + command.getItemList().stream().map(item -> String.valueOf(item.getBookId())).collect(Collectors.joining(", ")));
        }
        if (optionalOrder.isEmpty()) {
            errorList.add("No order found for Id:" + command.getOrderId());
        }

        if (errorList.isEmpty()) {
            Order order = optionalOrder.get();
            order.setLastUpdatedAt(LocalDateTime.now());
            order.setItemList(orderItemList);
            Order savedOrder = repository.save(order);
            return OrderResponse.success(savedOrder.getId());
        }
        return OrderResponse.failure(errorList);
    }

    private List<OrderItem> getOrderItemList(List<PlaceOrderItem> command) {
        List<OrderItem> orderItemList = command
                .stream()
                .filter(p -> catalogUseCase.findById(p.getBookId()).isPresent())
                .map(placeOrderItem -> new OrderItem(catalogUseCase.findById(placeOrderItem.getBookId()).get(), placeOrderItem.getQuantity()))
                .toList();
        return orderItemList;
    }

}
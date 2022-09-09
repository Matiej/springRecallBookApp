package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.order.application.port.PlaceOrderCommand;
import com.testaarosa.springRecallBookApp.order.application.port.PlaceOrderResponse;
import com.testaarosa.springRecallBookApp.order.application.port.PlaceOrderUseCase;
import com.testaarosa.springRecallBookApp.order.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.order.domain.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {
    private final OrderRepository repository;
    private final RecipientUseCase recipientUseCase;
    private final CatalogUseCase catalogUseCase;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        List<String> errorList = new ArrayList<>();

        List<OrderItem> orderItemList = command.getItemList()
                .stream()
                .filter(p -> catalogUseCase.findById(p.getBookId()).isPresent())
                .map(placeOrderItem -> new OrderItem(catalogUseCase.findById(placeOrderItem.getBookId()).get(), placeOrderItem.getQuantity()))
                .toList();

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
                    .build();
            Order savedOrder = repository.save(order);
            return PlaceOrderResponse.success(savedOrder.getId());
        }
        return PlaceOrderResponse.failure(errorList);
    }
}

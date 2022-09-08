package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.order.application.port.PlaceOrderCommand;
import com.testaarosa.springRecallBookApp.order.application.port.PlaceOrderResponse;
import com.testaarosa.springRecallBookApp.order.application.port.PlaceOrderUseCase;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderRepository;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {
    private final OrderRepository repository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Order order = Order.builder()
                .orderStatus(OrderStatus.NEW)
                .recipient(command.getRecipient())
                .itemList(command.getItemList())
                .build();
        Order savedOrder = repository.save(order);
        return PlaceOrderResponse.success(savedOrder.getId());
    }
}

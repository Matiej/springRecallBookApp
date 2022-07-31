package com.testaarosa.spirngRecallBookApp.order.application;

import com.testaarosa.spirngRecallBookApp.order.application.port.PlaceOrderCommand;
import com.testaarosa.spirngRecallBookApp.order.application.port.PlaceOrderResponse;
import com.testaarosa.spirngRecallBookApp.order.application.port.PlaceOrderUseCase;
import com.testaarosa.spirngRecallBookApp.order.domain.Order;
import com.testaarosa.spirngRecallBookApp.order.domain.OrderRepository;
import com.testaarosa.spirngRecallBookApp.order.domain.OrderStatus;
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

package com.testaarosa.springRecallBookApp.order.application.port;

import com.testaarosa.springRecallBookApp.order.controller.RestUpdateOrderStatusCommand;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;

import java.util.Optional;

public interface OrderUseCase {

    OrderResponse placeOrder(PlaceOrderCommand command);
    OrderResponse updateOrder(UpdateOrderCommand command);

    void removeOrderById(Long id);

    OrderResponse updateOrderStatus(Long id, String orderStatus);
}

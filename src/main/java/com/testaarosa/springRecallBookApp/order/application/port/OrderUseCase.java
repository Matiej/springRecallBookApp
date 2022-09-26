package com.testaarosa.springRecallBookApp.order.application.port;

import com.testaarosa.springRecallBookApp.order.application.OrderResponse;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderCommand;
import com.testaarosa.springRecallBookApp.order.application.UpdateOrderCommand;

public interface OrderUseCase {

    OrderResponse placeOrder(PlaceOrderCommand command);
    OrderResponse updateOrderItems(UpdateOrderCommand command);

    void removeOrderById(Long id);

    OrderResponse updateOrderStatus(Long id, String orderStatus);
}

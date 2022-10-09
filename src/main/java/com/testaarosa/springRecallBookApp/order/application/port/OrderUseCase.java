package com.testaarosa.springRecallBookApp.order.application.port;

import com.testaarosa.springRecallBookApp.order.application.OrderResponse;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderCommand;
import com.testaarosa.springRecallBookApp.order.application.UpdateOrderItemsCommand;
import com.testaarosa.springRecallBookApp.order.application.UpdateOrderStatusCommand;

public interface OrderUseCase {

    OrderResponse placeOrder(PlaceOrderCommand command);
    OrderResponse updateOrderItems(UpdateOrderItemsCommand command);

    void removeOrderById(Long id);

    OrderResponse updateOrderStatus(UpdateOrderStatusCommand command);

    OrderResponse findById(Long id);
}

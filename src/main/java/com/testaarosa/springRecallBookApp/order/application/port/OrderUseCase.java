package com.testaarosa.springRecallBookApp.order.application.port;

public interface OrderUseCase {

    OrderResponse placeOrder(PlaceOrderCommand command);
    OrderResponse updateOrder(UpdateOrderCommand command);

    void removeOrderById(Long id);
}

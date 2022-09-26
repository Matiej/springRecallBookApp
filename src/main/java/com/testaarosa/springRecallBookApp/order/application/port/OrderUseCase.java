package com.testaarosa.springRecallBookApp.order.application.port;

public interface OrderUseCase {

    OrderResponse placeOrder(PlaceOrderCommand command);
    OrderResponse updateOrderItems(UpdateOrderCommand command);

    void removeOrderById(Long id);

    OrderResponse updateOrderStatus(Long id, String orderStatus);
}

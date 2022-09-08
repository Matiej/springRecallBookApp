package com.testaarosa.springRecallBookApp.order.application.port;

public interface PlaceOrderUseCase {

    PlaceOrderResponse placeOrder(PlaceOrderCommand command);
}

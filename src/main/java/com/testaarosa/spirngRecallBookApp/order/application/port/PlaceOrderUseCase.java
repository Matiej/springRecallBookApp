package com.testaarosa.spirngRecallBookApp.order.application.port;

public interface PlaceOrderUseCase {

    PlaceOrderResponse placeOrder(PlaceOrderCommand command);
}

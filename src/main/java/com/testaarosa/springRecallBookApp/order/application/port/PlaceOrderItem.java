package com.testaarosa.springRecallBookApp.order.application.port;

import lombok.Value;

@Value
public class PlaceOrderItem {
    Long bookId;
    int quantity;
}

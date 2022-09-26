package com.testaarosa.springRecallBookApp.order.application;

import lombok.Value;

@Value
public class PlaceOrderItem {
    Long bookId;
    int quantity;
}

package com.testaarosa.springRecallBookApp.catalog.application.port;

import lombok.Value;

@Value
public class PlaceOrderItem {
    Long bookId;
    int quantity;
}

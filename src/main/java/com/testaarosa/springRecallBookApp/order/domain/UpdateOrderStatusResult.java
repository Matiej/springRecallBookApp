package com.testaarosa.springRecallBookApp.order.domain;

import lombok.Value;

@Value
public class UpdateOrderStatusResult {
    OrderStatus orderStatus;
    boolean revoked;

    static UpdateOrderStatusResult ok(OrderStatus newOrderStatus) {
        return new UpdateOrderStatusResult(newOrderStatus, false);
    }

    static UpdateOrderStatusResult revoked(OrderStatus newOrderStatus) {
        return new UpdateOrderStatusResult(newOrderStatus, true);
    }
}

package com.testaarosa.springRecallBookApp.order.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderResponse {
    boolean success;
    Long orderId;
    String error;

    public static OrderResponse success(Long orderId) {
        return new OrderResponse(true, orderId, null);
    }

    public static OrderResponse failure(String error, Long orderId)
    {
        return new OrderResponse(false, orderId, error);
    }
}

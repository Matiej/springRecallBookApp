package com.testaarosa.springRecallBookApp.order.application;

import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder
public class OrderResponse {
    boolean success;
    Long orderId;
    List<String> errorList;

    public static OrderResponse success(Long orderId) {
        return new OrderResponse(true, orderId, Collections.emptyList());
    }

    public static OrderResponse failure(List<String> errors) {
        return new OrderResponse(false, null, errors);
    }
}

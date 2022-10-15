package com.testaarosa.springRecallBookApp.order.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@Builder
public class OrderResponse {
    boolean success;
    Long orderId;
    String errorMessage;
    OrderErrorStatus errorStatus;

    public static OrderResponse success(Long orderId) {
        return new OrderResponse(true, orderId, null, null);
    }

    public static OrderResponse failure(Long orderId, String errorMessage, OrderErrorStatus status) {
        return new OrderResponse(false, orderId, errorMessage, status);
    }

    @AllArgsConstructor
    @Getter
    public enum OrderErrorStatus {
        NOT_FOUND(HttpStatus.NOT_FOUND),
        FORBIDDEN(HttpStatus.FORBIDDEN);

        private final HttpStatus status;
    }
}

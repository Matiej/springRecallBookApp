package com.testaarosa.spirngRecallBookApp.order.application.port;

import lombok.Builder;
import lombok.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Value
@Builder
public class PlaceOrderResponse {
    boolean success;
    Long orderId;
    List<String> errorList;

    public static PlaceOrderResponse success(Long orderId) {
        return new PlaceOrderResponse(true, orderId, Collections.emptyList());
    }

    public static PlaceOrderResponse failure(String... errors) {
        return new PlaceOrderResponse(false, null, Arrays.asList(errors));
    }
}

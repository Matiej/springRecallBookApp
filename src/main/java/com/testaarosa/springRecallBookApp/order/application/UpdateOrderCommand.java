package com.testaarosa.springRecallBookApp.order.application;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class UpdateOrderCommand {
    @Singular("item")
    List<PlaceOrderItem> itemList;
    Long orderId;

}

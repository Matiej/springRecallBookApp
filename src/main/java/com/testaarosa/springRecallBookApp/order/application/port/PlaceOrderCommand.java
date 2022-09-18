package com.testaarosa.springRecallBookApp.order.application.port;

import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class PlaceOrderCommand {
    @Singular("item")
    List<PlaceOrderItem> itemList;
    Long recipientId;
    OrderStatus orderStatus;
}

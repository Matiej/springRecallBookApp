package com.testaarosa.spirngRecallBookApp.order.application.port;

import com.testaarosa.spirngRecallBookApp.order.domain.OrderItem;
import com.testaarosa.spirngRecallBookApp.order.domain.OrderStatus;
import com.testaarosa.spirngRecallBookApp.order.domain.Recipient;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Builder
@Value
public
class PlaceOrderCommand {
    @Singular("item")
    List<OrderItem> itemList;
    Recipient recipient;
    OrderStatus orderStatus;
}

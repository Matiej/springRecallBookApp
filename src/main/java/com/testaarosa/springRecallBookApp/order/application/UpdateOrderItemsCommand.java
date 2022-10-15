package com.testaarosa.springRecallBookApp.order.application;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Builder
@Value
public class UpdateOrderItemsCommand {
    @Singular("item")
    List<PlaceOrderItem> itemList;
    Long orderId;
    User user;

}

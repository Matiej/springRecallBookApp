package com.testaarosa.springRecallBookApp.order.controller;

import com.testaarosa.springRecallBookApp.order.application.UpdateOrderItemsCommand;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
class RestUpdateOrderCommand extends RestOrderCommand {

    UpdateOrderItemsCommand toUpdateOrderCommand(Long orderId, UserDetails user) {
        return UpdateOrderItemsCommand.builder()
                .itemList(getPlaceOrderItems())
                .orderId(orderId)
                .user(user)
                .build();

    }
}

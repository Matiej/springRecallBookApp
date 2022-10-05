package com.testaarosa.springRecallBookApp.order.controller;

import com.testaarosa.springRecallBookApp.order.application.UpdateOrderItemsCommand;
import lombok.Data;

@Data
class RestUpdateOrderCommand extends RestOrderCommand {

    UpdateOrderItemsCommand toUpdateOrderCommand(Long orderId, String recipientEmail) {
        return UpdateOrderItemsCommand.builder()
                .itemList(getPlaceOrderItems())
                .orderId(orderId)
                .recipientEmail(recipientEmail)
                .build();

    }
}

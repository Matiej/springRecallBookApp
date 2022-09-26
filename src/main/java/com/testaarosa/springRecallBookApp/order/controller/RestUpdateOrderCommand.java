package com.testaarosa.springRecallBookApp.order.controller;

import com.testaarosa.springRecallBookApp.order.application.UpdateOrderCommand;
import lombok.Data;

@Data
class RestUpdateOrderCommand extends RestOrderCommand {

    UpdateOrderCommand toUpdateOrderCommand(Long orderId) {
        return UpdateOrderCommand.builder()
                .itemList(getPlaceOrderItems())
                .orderId(orderId)
                .build();

    }
}

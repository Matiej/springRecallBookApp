package com.testaarosa.springRecallBookApp.order.controller;

import com.testaarosa.springRecallBookApp.order.application.port.UpdateOrderCommand;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
class RestUpdateOrderCommand extends RestOrderCommand {
//    @NotNull(message = "OrderId filed can't be null")
//    @Min(1)
//    private Long orderId;

    UpdateOrderCommand toUpdateOrderCommand(Long orderId) {
        return UpdateOrderCommand.builder()
                .itemList(getPlaceOrderItems())
                .orderId(orderId)
                .build();

    }
}

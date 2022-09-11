package com.testaarosa.springRecallBookApp.order.controller;

import com.testaarosa.springRecallBookApp.order.application.port.PlaceOrderCommand;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
@Data
class RestPlaceOrderCommand extends RestOrderCommand{

    @NotNull(message = "RecipientId filed can't be null")
    @Min(1)
    private Long recipientId;

    PlaceOrderCommand toPlaceOrderCommand() {
        return PlaceOrderCommand.builder()
                .itemList(getPlaceOrderItems())
                .recipientId(recipientId)
                .orderStatus(OrderStatus.NEW)
                .build();
    }
}

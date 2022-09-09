package com.testaarosa.springRecallBookApp.order.controller;

import com.testaarosa.springRecallBookApp.catalog.application.port.PlaceOrderItem;
import com.testaarosa.springRecallBookApp.order.application.port.PlaceOrderCommand;
import com.testaarosa.springRecallBookApp.order.domain.OrderItem;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
class RestPlaceOrderCommand {
    @NotNull(message = "List of ordered items can't be null")
    private List<RestOrderItem> restOrderItemList;
    @NotNull(message = "RecipientId filed can't be null")
    @Min(1)
    private Long recipientId;

    PlaceOrderCommand toPlaceOrderCommand() {
        List<PlaceOrderItem> orderItemList = restOrderItemList.stream()
                .map(restOrderItem -> new PlaceOrderItem(restOrderItem.getBookId(), restOrderItem.getQuantity())).toList();
        return PlaceOrderCommand.builder()
                .itemList(orderItemList)
                .recipientId(recipientId)
                .orderStatus(OrderStatus.NEW)
                .build();
    }

}

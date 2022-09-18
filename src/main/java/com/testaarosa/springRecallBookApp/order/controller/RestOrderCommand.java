package com.testaarosa.springRecallBookApp.order.controller;

import com.testaarosa.springRecallBookApp.order.application.port.PlaceOrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class RestOrderCommand {
    @NotNull(message = "List of ordered items can't be null")
    private List<RestOrderItem> restOrderItemList;

    protected List<PlaceOrderItem> getPlaceOrderItems() {
        return restOrderItemList.stream()
                .map(restOrderItem -> new PlaceOrderItem(restOrderItem.getBookId(), restOrderItem.getQuantity())).toList();
    }

}

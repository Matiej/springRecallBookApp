package com.testaarosa.springRecallBookApp.order.controller;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RestUpdateOrderStatusCommand {
    @NotNull(message = "Order status can't be null.")
    private String orderStatus;
}

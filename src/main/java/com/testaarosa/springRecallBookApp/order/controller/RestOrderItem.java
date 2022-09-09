package com.testaarosa.springRecallBookApp.order.controller;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Builder
@Value
public class RestOrderItem {
    @NotNull(message = "bookId field can't be null")
    @Min(1)
    Long bookId;
    @NotNull(message = "quantity field can't be null")
    @Min(1)
    int quantity;
}

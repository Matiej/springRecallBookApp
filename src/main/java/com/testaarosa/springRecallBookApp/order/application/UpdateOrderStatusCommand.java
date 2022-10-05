package com.testaarosa.springRecallBookApp.order.application;


import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateOrderStatusCommand {
    Long orderId;
    OrderStatus orderStatus;
    String recipientEmail;
}

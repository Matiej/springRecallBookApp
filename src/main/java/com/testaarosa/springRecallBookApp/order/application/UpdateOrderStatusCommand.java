package com.testaarosa.springRecallBookApp.order.application;


import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Value;
import org.springframework.security.core.userdetails.User;

@Value
@Builder
public class UpdateOrderStatusCommand {
    Long orderId;
    OrderStatus orderStatus;
//    String recipientEmail;
    User user;
}

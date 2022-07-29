package com.testaarosa.spirngRecallBookApp.order.application.port;

import com.testaarosa.spirngRecallBookApp.order.domain.OrderItem;
import com.testaarosa.spirngRecallBookApp.order.domain.OrderStatus;
import com.testaarosa.spirngRecallBookApp.order.domain.Recipient;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class CreateOrderCommand {
    Long id;
    List<OrderItem> itemList;
    Recipient recipient;
    OrderStatus orderStatus;
    LocalDateTime createdAt;
}

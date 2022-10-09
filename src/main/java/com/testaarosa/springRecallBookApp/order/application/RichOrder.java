package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderItem;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
public class RichOrder {
    Long id;
    OrderStatus orderStatus;
    Set<OrderItem> orderItems;
    Recipient recipient;
    LocalDateTime createdAt;

    public BigDecimal totalPrice() {
        return orderItems.stream()
                .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static RichOrder toRichOrder(Order order) {
        return RichOrder.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus())
                .orderItems(order.getItems())
                .recipient(order.getRecipient())
                .createdAt(order.getCreatedAt())
                .build();
    }
}

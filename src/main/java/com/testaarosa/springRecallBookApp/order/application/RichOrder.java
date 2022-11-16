package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.order.application.price.OrderPrice;
import com.testaarosa.springRecallBookApp.order.domain.Delivery;
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
    Delivery delivery;
    OrderPrice orderPrice;
    BigDecimal finalPrice;

    public static RichOrder toRichOrder(Order order) {
        return RichOrder.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus())
                .orderItems(order.getOrderItems())
                .recipient(order.getRecipient())
                .createdAt(order.getCreatedAt())
                .delivery(order.getDelivery())
                .build();
    }

    public static RichOrder toRichOrderWithFinalPrice(OrderPrice orderPrice, Order order) {
        return RichOrder.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus())
                .orderItems(order.getOrderItems())
                .recipient(order.getRecipient())
                .createdAt(order.getCreatedAt())
                .delivery(order.getDelivery())
                .orderPrice(orderPrice)
                .finalPrice(orderPrice.finalPrice())
                .build();
    }
}

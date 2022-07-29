package com.testaarosa.spirngRecallBookApp.order.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Order {
    private Long id;
    private List<OrderItem> itemList;
    private Recipient recipient;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;

    public Order(List<OrderItem> itemList, Recipient recipient, OrderStatus orderStatus, LocalDateTime createdAt) {
        this.itemList = itemList;
        this.recipient = recipient;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }

    public BigDecimal totalPrice() {
        return itemList.stream()
                .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}

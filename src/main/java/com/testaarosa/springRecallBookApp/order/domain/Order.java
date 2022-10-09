package com.testaarosa.springRecallBookApp.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@Entity(name = "Orders")
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private Set<OrderItem> orderItems = new HashSet<>();

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @ToString.Exclude
    @JoinColumn(name = "recipient_id")
    @JsonIgnoreProperties(value = "orders")
    private Recipient recipient;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.NEW;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Delivery delivery = Delivery.COURIER;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Order order = (Order) o;
        return Objects.equals(orderItems, order.orderItems) && orderStatus == order.orderStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderItems, orderStatus);
    }

    public BigDecimal getItemsPrice() {
        return orderItems.stream()
                .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDeliverPrice() {
        return delivery.getPrice();
    }

    public void replaceOrderItems(Set<OrderItem> orderItems) {
        orderItems.clear();
        orderItems.addAll(orderItems);
    }

    public UpdateOrderStatusResult updateOrderStatus(OrderStatus newStatus) {
        UpdateOrderStatusResult updateOrderStatusResult = orderStatus.updateOrderStatus(newStatus);
        this.setOrderStatus(updateOrderStatusResult.getOrderStatus());
        return updateOrderStatusResult;
    }
}



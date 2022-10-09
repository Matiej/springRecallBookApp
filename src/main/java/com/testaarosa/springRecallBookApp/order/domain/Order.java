package com.testaarosa.springRecallBookApp.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import lombok.*;

import javax.persistence.*;
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
    private Set<OrderItem> items = new HashSet<>();
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @ToString.Exclude
    @JoinColumn(name = "recipient_id")
    @JsonIgnoreProperties(value = "orders")
    private Recipient recipient;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Order order = (Order) o;
        return Objects.equals(items, order.items) && orderStatus == order.orderStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), items, orderStatus);
    }

    public void replaceOrderItems(Set<OrderItem> orderItems) {
        items.clear();
        items.addAll(orderItems);
    }

    public UpdateOrderStatusResult updateOrderStatus(OrderStatus newStatus) {
        UpdateOrderStatusResult updateOrderStatusResult = orderStatus.updateOrderStatus(newStatus);
        this.setOrderStatus(updateOrderStatusResult.getOrderStatus());
        return updateOrderStatusResult;
    }
}



package com.testaarosa.springRecallBookApp.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
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
    private Set<OrderItem> itemList = new HashSet<>();
    @ManyToOne()
    @ToString.Exclude
    @JoinColumn(name = "recipient_id")
    @JsonIgnoreProperties(value = "orders")
    private Recipient recipient;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public BigDecimal totalPrice() {
        return itemList.stream()
                .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Order order = (Order) o;
        return Objects.equals(itemList, order.itemList) && orderStatus == order.orderStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemList, orderStatus);
    }

    public void replaceOrderItems(Set<OrderItem> orderItems) {
        itemList.clear();
        itemList.addAll(orderItems);
    }


    public void updateOrderStatus(OrderStatus newStatus) {
        this.setOrderStatus(orderStatus.updateOrderStatus(newStatus));
    }
}



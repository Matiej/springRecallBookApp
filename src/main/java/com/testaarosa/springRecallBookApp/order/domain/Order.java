package com.testaarosa.springRecallBookApp.order.domain;

import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@Entity(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private List<OrderItem> itemList;
    private transient Recipient recipient;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    public BigDecimal totalPrice() {
        return itemList.stream()
                .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(itemList, order.itemList) && Objects.equals(recipient, order.recipient) && orderStatus == order.orderStatus && Objects.equals(createdAt, order.createdAt) && Objects.equals(lastUpdatedAt, order.lastUpdatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemList, recipient, orderStatus, createdAt, lastUpdatedAt);
    }
}



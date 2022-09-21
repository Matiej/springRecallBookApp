package com.testaarosa.springRecallBookApp.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@Entity(name = "Orders")
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private List<OrderItem> itemList = new ArrayList<>();
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
        return Objects.equals(id, order.id) && Objects.equals(itemList, order.itemList) && orderStatus == order.orderStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, itemList, orderStatus);
    }

    public void replaceOrderItems(List<OrderItem> orderItemList) {
        itemList.clear();
        itemList.addAll(orderItemList);
    }


}



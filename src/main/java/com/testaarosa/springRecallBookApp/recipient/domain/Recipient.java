package com.testaarosa.springRecallBookApp.recipient.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Recipient {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<Order> orderList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipient recipient = (Recipient) o;
        return Objects.equals(id, recipient.id) && Objects.equals(name, recipient.name) && Objects.equals(phone, recipient.phone) && Objects.equals(street, recipient.street) && Objects.equals(city, recipient.city) && Objects.equals(zipCode, recipient.zipCode) && Objects.equals(email, recipient.email) && Objects.equals(createdAt, recipient.createdAt) && Objects.equals(lastUpdatedAt, recipient.lastUpdatedAt) && Objects.equals(orderList, recipient.orderList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phone, street, city, zipCode, email, createdAt, lastUpdatedAt, orderList);
    }

    public boolean addOrder(Order order) {
        if(orderList == null) {
            orderList = new ArrayList<>();
        }
        return orderList.add(order);
    }
}

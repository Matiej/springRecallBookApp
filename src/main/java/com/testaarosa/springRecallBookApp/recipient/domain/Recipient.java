package com.testaarosa.springRecallBookApp.recipient.domain;

import com.testaarosa.springRecallBookApp.order.domain.Order;
import lombok.*;

import javax.persistence.*;
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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipient_id")
    private List<Order> orderList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipient recipient = (Recipient) o;
        return Objects.equals(id, recipient.id) && Objects.equals(name, recipient.name) && Objects.equals(phone, recipient.phone) && Objects.equals(street, recipient.street) && Objects.equals(city, recipient.city) && Objects.equals(zipCode, recipient.zipCode) && Objects.equals(email, recipient.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phone, street, city, zipCode, email);
    }

    public boolean addOrder(Order order) {
        if(orderList == null) {
            orderList = new ArrayList<>();
        }
        return orderList.add(order);
    }
}

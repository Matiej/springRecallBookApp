package com.testaarosa.springRecallBookApp.recipient.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Recipient extends BaseEntity {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastName;
    private String phone;
    private String email;
    private RecipientAddress recipientAddress;
    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "recipient")
    @ToString.Exclude
    @JsonIgnoreProperties(value = "recipient")
    private Set<Order> orders = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Recipient recipient = (Recipient) o;
        return Objects.equals(id, recipient.id) && Objects.equals(name, recipient.name) && Objects.equals(lastName, recipient.lastName) && Objects.equals(phone, recipient.phone) && Objects.equals(email, recipient.email) && Objects.equals(recipientAddress, recipient.recipientAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, lastName, phone, email, recipientAddress);
    }

    public void addOrder(Order order) {
        orders.add(order);
    }
}

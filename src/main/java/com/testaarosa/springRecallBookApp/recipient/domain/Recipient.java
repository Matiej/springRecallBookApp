package com.testaarosa.springRecallBookApp.recipient.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
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
    @Singular
    private Set<Order> orders = new HashSet<>();

    public Recipient(String name, String lastName, String phone, String email, RecipientAddress recipientAddress) {
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.recipientAddress = recipientAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Recipient recipient = (Recipient) o;
        return Objects.equals(name, recipient.name) && Objects.equals(lastName, recipient.lastName) && Objects.equals(phone, recipient.phone)
                && Objects.equals(email, recipient.email) && Objects.equals(recipientAddress, recipient.recipientAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, lastName, phone, email, recipientAddress);
    }

    public void addOrder(Order order) {
        this.getOrders().add(order);
    }

    public void updateFields(Recipient newRecipient) {
        if (!newRecipient.getName().equalsIgnoreCase(name)) {
            this.setName(newRecipient.getName());
        }
        if (!newRecipient.getLastName().equalsIgnoreCase(lastName)) {
            this.setLastName(newRecipient.getLastName());
        }
        if (!newRecipient.getPhone().equalsIgnoreCase(phone)) {
            this.setPhone(newRecipient.getPhone());
        }
        this.setRecipientAddress(newRecipient.getRecipientAddress());
    }
}

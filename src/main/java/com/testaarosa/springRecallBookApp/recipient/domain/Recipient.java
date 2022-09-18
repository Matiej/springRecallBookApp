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
import java.util.HashSet;
import java.util.Set;

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
    private String lastName;
    private String phone;
    private String email;
    private RecipientAddress recipientAddress;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;
    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "recipient")
    @ToString.Exclude
    @JsonIgnore
    private Set<Order> orders;


    public boolean addOrder(Order order) {
        if (orders == null) {
            orders = new HashSet<>();
        }
        return orders.add(order);
    }
}

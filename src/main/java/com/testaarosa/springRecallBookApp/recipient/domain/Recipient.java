package com.testaarosa.springRecallBookApp.recipient.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
}

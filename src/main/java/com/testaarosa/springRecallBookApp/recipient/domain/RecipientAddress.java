package com.testaarosa.springRecallBookApp.recipient.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class RecipientAddress {
    private String street;
    private String buildingNumber;
    private String apartmentNumber;
    private String district;
    private String city;
    private String zipCode;
}

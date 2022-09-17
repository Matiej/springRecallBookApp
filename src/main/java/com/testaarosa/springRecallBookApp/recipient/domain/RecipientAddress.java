package com.testaarosa.springRecallBookApp.recipient.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class RecipientAddress {
    private String street;
    private String buildingNumber;
    private String apartmentNumber;
    private String district;
    private String city;
    private String zipCode;


}

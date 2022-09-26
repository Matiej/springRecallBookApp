package com.testaarosa.springRecallBookApp.recipient.application;

import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientAddress;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class SaveRecipientCommand {
    String name;
    String lastName;
    String phone;
    String email;
    String street;
    String buildingNumber;
    String apartmentNumber;
    String district;
    String city;
    String zipCode;

    public Recipient toRecipient() {
        return Recipient.builder()
                .name(name)
                .lastName(lastName)
                .phone(phone)
                .email(email)
                .recipientAddress(RecipientAddress.builder()
                        .street(street)
                        .buildingNumber(buildingNumber)
                        .apartmentNumber(apartmentNumber)
                        .district(district)
                        .city(city)
                        .zipCode(zipCode)
                        .build())
                .build();
    }
}

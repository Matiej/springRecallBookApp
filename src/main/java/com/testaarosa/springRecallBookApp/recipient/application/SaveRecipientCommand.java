package com.testaarosa.springRecallBookApp.recipient.application;

import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientAddress;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class SaveRecipientCommand extends RecipientCommand {

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

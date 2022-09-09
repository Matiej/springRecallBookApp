package com.testaarosa.springRecallBookApp.order.application.port;

import com.testaarosa.springRecallBookApp.order.domain.Recipient;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class SaveRecipientCommand {
    String name;
    String phone;
    String street;
    String city;
    String zipCode;
    String email;

    public Recipient toRecipient() {
        return Recipient.builder()
                .name(name)
                .phone(phone)
                .street(street)
                .city(city)
                .zipCode(zipCode)
                .email(email)
                .build();
    }
}

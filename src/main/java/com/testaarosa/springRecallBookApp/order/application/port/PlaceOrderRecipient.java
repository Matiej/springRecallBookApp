package com.testaarosa.springRecallBookApp.order.application.port;

import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientAddress;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlaceOrderRecipient {
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
        return new Recipient(
                this.getName(),
                this.getLastName(),
                this.getPhone(),
                this.getEmail(),
                new RecipientAddress(this.getStreet(),
                        this.getBuildingNumber(),
                        this.getApartmentNumber(),
                        this.getDistrict(),
                        this.getCity(),
                        this.getZipCode())
        );
    }
}

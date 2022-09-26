package com.testaarosa.springRecallBookApp.recipient.application;

import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientAddress;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
@Builder(builderMethodName = "hiddenBuilder")
public class UpdateRecipientCommand {
    Long id;
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

    public Recipient updateRecipientFields(Recipient recipient) {
        if (StringUtils.isNoneBlank(name)) {
            recipient.setName(name);
        }
        if (StringUtils.isNoneBlank(lastName)) {
            recipient.setLastName(lastName);
        }
        if (StringUtils.isNoneBlank(phone)) {
            recipient.setPhone(phone);
        }
        if (StringUtils.isNoneBlank(email)) {
            recipient.setEmail(email);
        }

        RecipientAddress recipientAddress = recipient.getRecipientAddress();

        if (StringUtils.isNoneBlank(street)) {
            recipientAddress.setStreet(street);
        }
        if (StringUtils.isNoneBlank(buildingNumber)) {
            recipientAddress.setBuildingNumber(buildingNumber);
        }
        if (StringUtils.isNoneBlank(apartmentNumber)) {
            recipientAddress.setApartmentNumber(apartmentNumber);
        }
        if (StringUtils.isNoneBlank(district)) {
            recipientAddress.setDistrict(district);
        }
        if (StringUtils.isNoneBlank(city)) {
            recipientAddress.setCity(city);
        }
        if (StringUtils.isNoneBlank(zipCode)) {
            recipientAddress.setZipCode(zipCode);
        }
        recipient.setRecipientAddress(recipientAddress);
        return recipient;
    }

    public static UpdateRecipientCommandBuilder builder(Long id) {
        return hiddenBuilder().id(id);
    }
}

package com.testaarosa.springRecallBookApp.recipient.application;

import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientAddress;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;

@ToString
@Getter
@SuperBuilder(builderMethodName = "hiddenBuilder")
public class UpdateRecipientCommand extends RecipientCommand {
    private Long id;
    private UserDetails user;

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
        // todo Maciek - no possiblity to update email - must be the same as user email. Maybe not. Think about how it should be
//        if (StringUtils.isNoneBlank(email)) {
//            recipient.setEmail(email);
//        }

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
}

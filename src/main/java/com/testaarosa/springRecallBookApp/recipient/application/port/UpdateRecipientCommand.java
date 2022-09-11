package com.testaarosa.springRecallBookApp.recipient.application.port;

import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
@Builder(builderMethodName = "hiddenBuilder")
public class UpdateRecipientCommand {
    Long id;
    String name;
    String phone;
    String street;
    String city;
    String zipCode;
    String email;

    public Recipient updateRecipientFields(Recipient recipient) {
        if (StringUtils.isNoneBlank(name)) {
            recipient.setName(name);
        }
        if (StringUtils.isNoneBlank(phone)) {
            recipient.setPhone(phone);
        }
        if (StringUtils.isNoneBlank(street)) {
            recipient.setStreet(street);
        }
        if (StringUtils.isNoneBlank(city)) {
            recipient.setCity(city);
        }
        if (StringUtils.isNoneBlank(zipCode)) {
            recipient.setZipCode(zipCode);
        }
        if (StringUtils.isNoneBlank(email)) {
            recipient.setEmail(email);
        }
        return recipient;
    }

    public static UpdateRecipientCommandBuilder builder(Long id) {
        return hiddenBuilder().id(id);
    }
}

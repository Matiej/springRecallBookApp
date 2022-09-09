package com.testaarosa.springRecallBookApp.order.application.port;

import lombok.Builder;
import lombok.Value;

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

    public static UpdateRecipientCommandBuilder builder(Long id) {
        return hiddenBuilder().id(id);
    }
}

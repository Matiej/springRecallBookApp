package com.testaarosa.springRecallBookApp.recipient.controller;

import com.testaarosa.springRecallBookApp.recipient.application.port.SaveRecipientCommand;
import com.testaarosa.springRecallBookApp.recipient.application.port.UpdateRecipientCommand;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Builder
@Value
public class RestRecipientCommand {
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Name field can't be blank, empty or null")
    String name;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Phone field can't be blank, empty or null")
    String phone;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Street field can't be blank, empty or null")
    String street;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "City field can't be blank, empty or null")
    String city;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "ZipCode field can't be blank, empty or null")
    String zipCode;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Email field can't be blank, empty or null")
    String email;

    SaveRecipientCommand toSaveRecipientCommand() {
        return SaveRecipientCommand.builder()
                .name(name)
                .phone(phone)
                .street(street)
                .city(city)
                .zipCode(zipCode)
                .email(email)
                .build();
    }

    UpdateRecipientCommand toUpdateRecipientCommand(Long id) {
        return UpdateRecipientCommand.builder(id)
                .name(name)
                .phone(phone)
                .street(street)
                .city(city)
                .zipCode(zipCode)
                .email(email)
                .build();
    }
}

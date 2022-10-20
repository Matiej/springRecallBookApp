package com.testaarosa.springRecallBookApp.recipient.controller;

import com.testaarosa.springRecallBookApp.recipient.application.SaveRecipientCommand;
import com.testaarosa.springRecallBookApp.recipient.application.UpdateRecipientCommand;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Builder
@Value
class RestRecipientCommand {
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Name field can't be blank, empty or null")
    String name;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "LastName field can't be blank, empty or null")
    String lastName;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Phone field can't be blank, empty or null")
    String phone;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Email field can't be blank, empty or null")
    String email;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Street field can't be blank, empty or null")
    String street;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "buildingNumber field can't be blank, empty or null")
    String buildingNumber;
    String apartmentNumber;
    String district;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "City field can't be blank, empty or null")
    String city;
    @NotBlank(groups = {SaveRecipientGroup.class}, message = "zipCode field can't be blank, empty or null")
    String zipCode;

    SaveRecipientCommand toSaveRecipientCommand() {
        return SaveRecipientCommand.builder()
                .name(name)
                .lastName(lastName)
                .phone(phone)
                .email(email)
                .street(street)
                .buildingNumber(buildingNumber)
                .apartmentNumber(apartmentNumber)
                .district(district)
                .city(city)
                .zipCode(zipCode)
                .build();
    }

    UpdateRecipientCommand toUpdateRecipientCommand(Long id) {
        return (UpdateRecipientCommand) UpdateRecipientCommand.builder(id)
                .name(name)
                .phone(phone)
                .street(street)
                .city(city)
                .zipCode(zipCode)
                .email(email)
                .build();
    }
}

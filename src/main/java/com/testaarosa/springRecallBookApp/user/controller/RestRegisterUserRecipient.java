package com.testaarosa.springRecallBookApp.user.controller;

import com.testaarosa.springRecallBookApp.recipient.controller.SaveRecipientGroup;
import com.testaarosa.springRecallBookApp.user.application.RegisterUserCommand;
import com.testaarosa.springRecallBookApp.user.application.RegisterUserRecipientCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestRegisterUserRecipient extends RestRegisterUser {

    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Name field can't be blank, empty or null")
    private String name;

    @NotBlank(groups = {SaveRecipientGroup.class}, message = "LastName field can't be blank, empty or null")
    private String lastName;

    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Phone field can't be blank, empty or null")
    private String phone;

    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Email field can't be blank, empty or null")
    private String email;

    @NotBlank(groups = {SaveRecipientGroup.class}, message = "Street field can't be blank, empty or null")
    private String street;

    @NotBlank(groups = {SaveRecipientGroup.class}, message = "buildingNumber field can't be blank, empty or null")
    private String buildingNumber;

    private String apartmentNumber;
    private String district;

    @NotBlank(groups = {SaveRecipientGroup.class}, message = "City field can't be blank, empty or null")
    private String city;

    @NotBlank(groups = {SaveRecipientGroup.class}, message = "zipCode field can't be blank, empty or null")
    private String zipCode;

    public RegisterUserRecipientCommand toRegisterUserRecipientCommand() {
        return RegisterUserRecipientCommand.hiddenBuilder()
                .username(this.getUsername())
                .password(this.getPassword())
                .passwordMatch(this.getPasswordMatch())
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
}

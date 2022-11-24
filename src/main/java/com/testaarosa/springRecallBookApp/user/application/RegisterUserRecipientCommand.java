package com.testaarosa.springRecallBookApp.user.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder(builderMethodName = "hiddenBuilder")
public class RegisterUserRecipientCommand extends RegisterUserCommand{
    private String name;
    private String lastName;
    private String phone;
    private String email;
    private String street;
    private String buildingNumber;
    private String apartmentNumber;
    private String district;
    private String city;
    private String zipCode;
}

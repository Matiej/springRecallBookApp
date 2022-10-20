package com.testaarosa.springRecallBookApp.recipient.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter(AccessLevel.PROTECTED)
public abstract class RecipientCommand {
    protected String name;
    protected String lastName;
    protected String phone;
    protected String email;
    protected String street;
    protected String buildingNumber;
    protected String apartmentNumber;
    protected String district;
    protected String city;
    protected String zipCode;
}

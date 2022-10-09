package com.testaarosa.springRecallBookApp.order.controller;

import com.testaarosa.springRecallBookApp.order.application.PlaceOrderCommand;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderRecipient;
import com.testaarosa.springRecallBookApp.order.domain.Delivery;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import com.testaarosa.springRecallBookApp.recipient.controller.SaveRecipientGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
class RestPlaceOrderCommand extends RestOrderCommand{
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
    @NotNull(message = "Delivery option cannot be empty")
    Delivery delivery;


    PlaceOrderCommand toPlaceOrderCommand() {
        return PlaceOrderCommand.builder()
                .itemList(getPlaceOrderItems())
                .orderStatus(OrderStatus.NEW)
                .placeOrderRecipient(toPlaceOrderRecipient())
                .delivery(delivery)
                .build();
    }

    private PlaceOrderRecipient toPlaceOrderRecipient() {
        return PlaceOrderRecipient.builder()
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

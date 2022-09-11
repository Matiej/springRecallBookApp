package com.testaarosa.springRecallBookApp.recipient.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Builder
public class Recipient {
    @Setter
    private Long id;
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;


}

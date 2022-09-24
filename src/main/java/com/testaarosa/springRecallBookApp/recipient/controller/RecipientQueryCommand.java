package com.testaarosa.springRecallBookApp.recipient.controller;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RecipientQueryCommand {
    String name;
    String lastName;
    String zipCode;
    int limit;
}

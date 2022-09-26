package com.testaarosa.springRecallBookApp.recipient.application;

import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder
public class RecipientResponse {
    boolean success;
    Long orderId;
    List<String> errorList;

    public static RecipientResponse SUCCESS(Long orderId) {
        return new RecipientResponse(true, orderId, Collections.emptyList());
    }

    public static RecipientResponse FAILURE(List<String> errors) {
        return new RecipientResponse(false, null, errors);
    }
}

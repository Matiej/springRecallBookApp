package com.testaarosa.springRecallBookApp.recipient.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RecipientResponse {
    boolean success;
    Long orderId;
    String error;

    public static RecipientResponse SUCCESS(Long orderId) {
        return new RecipientResponse(true, orderId, null);
    }

    public static RecipientResponse FAILURE(String error) {
        return new RecipientResponse(false, null, error);
    }
}

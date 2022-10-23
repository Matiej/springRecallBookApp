package com.testaarosa.springRecallBookApp.recipient.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@Builder
public class RecipientResponse {
    boolean success;
    Long recipientId;
    String error;
    RecipientErrorStatus status;

    public static RecipientResponse SUCCESS(Long orderId) {
        return new RecipientResponse(true, orderId, null, null);
    }

    public static RecipientResponse FAILURE(String error, Long id, RecipientErrorStatus status) {
        return new RecipientResponse(false, id, error, status);
    }

    @AllArgsConstructor
    @Getter
    public enum RecipientErrorStatus {
        NOT_FOUND(HttpStatus.NOT_FOUND),
        FORBIDDEN(HttpStatus.FORBIDDEN);

        private final HttpStatus status;
    }
}

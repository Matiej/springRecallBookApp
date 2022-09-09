package com.testaarosa.springRecallBookApp.order.application.port;

import lombok.Builder;
import lombok.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Value
@Builder
public class UpdateRecipientResponse {
    boolean success;
    Long orderId;
    List<String> errorList;

    public static UpdateRecipientResponse success(Long orderId) {
        return new UpdateRecipientResponse(true, orderId, Collections.emptyList());
    }

    public static UpdateRecipientResponse failure(String... errors) {
        return new UpdateRecipientResponse(false, null, Arrays.asList(errors));
    }

}

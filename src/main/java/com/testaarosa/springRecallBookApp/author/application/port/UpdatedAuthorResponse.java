package com.testaarosa.springRecallBookApp.author.application.port;

import lombok.Value;

import java.util.Collections;
import java.util.List;
@Value
public class UpdatedAuthorResponse {
    boolean success;
    List<String> errorList;

    public static UpdatedAuthorResponse SUCCESS = new UpdatedAuthorResponse(true, Collections.emptyList());

    public static UpdatedAuthorResponse FAILURE(List<String> errorList) {
        return new UpdatedAuthorResponse(false, errorList);
    }
}

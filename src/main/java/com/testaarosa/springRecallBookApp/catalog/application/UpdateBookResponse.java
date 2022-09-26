package com.testaarosa.springRecallBookApp.catalog.application;

import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class UpdateBookResponse {
    boolean success;
    List<String> errorList;

    public static UpdateBookResponse SUCCESS = new UpdateBookResponse(true, Collections.emptyList());

    public static UpdateBookResponse FAILURE(List<String> errorList) {
        return new UpdateBookResponse(false, errorList);
    }
}

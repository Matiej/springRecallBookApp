package com.testaarosa.spirngRecallBookApp.catalog.application.port;

import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class UpdateBookResponse {
    boolean success;
    List<String> errorList;

    public static UpdateBookResponse SUCCESS = new UpdateBookResponse(true, Collections.emptyList());
}

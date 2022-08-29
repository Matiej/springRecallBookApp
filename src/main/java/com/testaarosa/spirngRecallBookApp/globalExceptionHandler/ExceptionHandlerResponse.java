package com.testaarosa.spirngRecallBookApp.globalExceptionHandler;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Value
public class ExceptionHandlerResponse {
    LocalDateTime errorTimeStamp;
    String message;
    @Singular("detail")
    List<ErrorDetailMessage> details;
    String statusCode;
    String status;
}

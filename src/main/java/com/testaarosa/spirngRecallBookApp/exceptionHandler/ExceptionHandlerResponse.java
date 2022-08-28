package com.testaarosa.spirngRecallBookApp.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ExceptionHandlerResponse {
    private LocalDateTime errorTimeStamp;
    private String message;
    private String details;
    private String statusCode;
    private String status;
}

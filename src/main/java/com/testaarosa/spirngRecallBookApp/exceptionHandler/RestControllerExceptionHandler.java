package com.testaarosa.spirngRecallBookApp.exceptionHandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestController
@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {


    //todo every exception handle like below
//    @ExceptionHandler({Exception.class})
//    public final ResponseEntity<Object> handleConstraintViolationException(Exception ex, WebRequest request) {
//        String message = "Validation error ==> ";
//        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
//        ExceptionHandlerResponse exceptionResponse = getExceptionHandlerResponse(ex, message, badRequest);
//        return ResponseEntity.status(badRequest)
//                .headers(getExceptionHeaders(badRequest.name(), message))
//                .body(exceptionResponse);
//    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = "error servlet params ";
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ExceptionHandlerResponse exceptionHandlerResponse = getExceptionHandlerResponse(ex, message, badRequest);
        return ResponseEntity.status(badRequest)
                .headers(getExceptionHeaders(badRequest.name(), message))
                .body(exceptionHandlerResponse);
    }

    private ExceptionHandlerResponse getExceptionHandlerResponse(Exception ex, String message, HttpStatus httpStatus) {
        String details = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException) {
            details = ex.getMessage().substring(ex.getMessage().indexOf(":") + 1);
        }
        return new ExceptionHandlerResponse(LocalDateTime.now().withNano(0), message, details, String.valueOf(httpStatus.value()), httpStatus.name());
    }

    private HttpHeaders getExceptionHeaders(String status, String message) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Status", status);
        httpHeaders.add("Message", message);
        return httpHeaders;
    }

}

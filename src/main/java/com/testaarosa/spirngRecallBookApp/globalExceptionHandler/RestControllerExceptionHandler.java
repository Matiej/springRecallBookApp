package com.testaarosa.spirngRecallBookApp.globalExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
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
        String message = "Method arguments error";
        log.error(message, ex);
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ExceptionHandlerResponse exceptionHandlerResponse = getExceptionHandlerResponse(ex, message, badRequest);
        return ResponseEntity.status(badRequest)
                .headers(getExceptionHeaders(badRequest.name(), message))
                .body(exceptionHandlerResponse);
    }

    private ExceptionHandlerResponse getExceptionHandlerResponse(Exception ex, String message, HttpStatus httpStatus) {

        ExceptionHandlerResponse.ExceptionHandlerResponseBuilder exceptionHandlerResponseBuilder = ExceptionHandlerResponse.builder();

        if (ex instanceof MethodArgumentNotValidException) {
            exceptionHandlerResponseBuilder.details(((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors()
                    .stream()
                    .map(err -> new MethodArgumentErrorDetailMessage(
                            ((FieldError) err).getField(),
                            ((FieldError) err).getRejectedValue(),
                            err.getDefaultMessage()))
                    .toList());
        } else {
            exceptionHandlerResponseBuilder.detail(new ErrorDetailMessage(ex.getMessage()));
        }

        return exceptionHandlerResponseBuilder
                .errorTimeStamp(LocalDateTime.now())
                .message(message)
                .statusCode(String.valueOf(httpStatus.value()))
                .status(httpStatus.name())
                .build();
    }

    private HttpHeaders getExceptionHeaders(String status, String message) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Status", status);
        httpHeaders.add("Message", message);
        return httpHeaders;
    }

}
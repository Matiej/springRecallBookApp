package com.testaarosa.springRecallBookApp.globalExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public final ResponseEntity<Object> handleHibernateException(RuntimeException rex, WebRequest request) {
        //todo prepared for database, uncoment code to
        String message = "";
//        if (rex instanceof FeignException) {
//            message = "Gov resource server error. Can not send and receive any report";
//            log.error(message, rex);
//        } else if (rex instanceof HibernateException) {
//            message = "Data Base server error. Can not get or save any report.";
//            log.error(message, rex);
//        } else {
        message = "External server error. ";
        log.error(message, rex);
        HttpStatus serviceUnavailable = HttpStatus.SERVICE_UNAVAILABLE;
        ExceptionHandlerResponse exceptionResponse = getExceptionHandlerResponse(rex, message, serviceUnavailable);
        return ResponseEntity.status(serviceUnavailable)
                .headers(getExceptionHeaders(serviceUnavailable.name(), message))
                .body(exceptionResponse);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public final ResponseEntity<Object> handleIllegalArgumentException(RuntimeException rex, WebRequest request) {
        String message = "Given argument error=> ";
        log.error(message, rex);
        HttpStatus serviceUnavailable = HttpStatus.BAD_REQUEST;
        ExceptionHandlerResponse exceptionResponse = getExceptionHandlerResponse(rex, message, serviceUnavailable);
        return ResponseEntity.status(serviceUnavailable)
                .headers(getExceptionHeaders(serviceUnavailable.name(), message))
                .body(exceptionResponse);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public final ResponseEntity<Object> handleConstraintViolationException(RuntimeException rex, WebRequest request) {
        String message = "Path parameter error. ";
        log.error(message, rex);
        HttpStatus serviceUnavailable = HttpStatus.BAD_REQUEST;
        ExceptionHandlerResponse exceptionResponse = getExceptionHandlerResponse(rex, message, serviceUnavailable);
        return ResponseEntity.status(serviceUnavailable)
                .headers(getExceptionHeaders(serviceUnavailable.name(), message))
                .body(exceptionResponse);
    }

    @ExceptionHandler({IOException.class})
    public final ResponseEntity<Object> handleIOExceptionException(Exception ex, WebRequest request) {
        String message = "IOException error ==> ";
        log.error(message, ex);
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ExceptionHandlerResponse exceptionResponse = getExceptionHandlerResponse(ex, message, badRequest);
        return ResponseEntity.status(badRequest)
                .headers(getExceptionHeaders(badRequest.name(), message))
                .body(exceptionResponse);
    }

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

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = "error servlet params ";
        log.error(message, ex);
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ExceptionHandlerResponse exceptionHandlerResponse = getExceptionHandlerResponse(ex, message, badRequest);
        return ResponseEntity.status(badRequest)
                .headers(getExceptionHeaders(HttpStatus.BAD_REQUEST.name(), message))
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
package com.testaarosa.spirngRecallBookApp.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
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
        String message = "Method arguments error";
        log.error(message, ex);
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ExceptionHandlerResponse exceptionHandlerResponse = getExceptionHandlerResponse(ex, message, badRequest);
        return ResponseEntity.status(badRequest)
                .headers(getExceptionHeaders(badRequest.name(), message))
                .body(exceptionHandlerResponse);
    }

    private ExceptionHandlerResponse getExceptionHandlerResponse(Exception ex, String message, HttpStatus httpStatus) {
        String details = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException) {
            List<MethodArgumentErrorDetailMessage> allErrors = ((MethodArgumentNotValidException) ex).getAllErrors()
                    .stream()
                    .map(err -> new MethodArgumentErrorDetailMessage(((FieldError) err).getField(),
                            ((FieldError) err).getRejectedValue(),
                            err.getDefaultMessage()))
                    .toList();
            details = allErrors.toString();
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

@Data
@AllArgsConstructor
class MethodArgumentErrorDetailMessage {
    private String fieldName;
    private Object rejectedValue;
    private String filedErrorMessage;

    @Override
    public String toString() {
        return new StringJoiner(", ", MethodArgumentErrorDetailMessage.class.getSimpleName() + "[", "]")
                .add("fieldName='" + fieldName + "'")
                .add("rejectedValue='" + rejectedValue + "'")
                .add("filedErrorMessage='" + filedErrorMessage + "'")
                .toString();
    }
}

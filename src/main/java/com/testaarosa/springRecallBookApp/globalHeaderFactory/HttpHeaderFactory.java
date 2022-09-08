package com.testaarosa.springRecallBookApp.globalHeaderFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public class HttpHeaderFactory {
    private static final String SUCCESSFUL = "Successful";


    public static HttpHeaders getSuccessfulHeaders(HttpStatus status, HttpMethod... allowMethods) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, Arrays.toString(allowMethods));
        httpHeaders.add(HeaderKey.STATUS.getHeaderKeyLabel(), String.valueOf(status));
        httpHeaders.add(HeaderKey.MESSAGE.getHeaderKeyLabel(), SUCCESSFUL);
        return httpHeaders;
    }
}



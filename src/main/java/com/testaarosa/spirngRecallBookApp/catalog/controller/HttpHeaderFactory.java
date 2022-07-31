package com.testaarosa.spirngRecallBookApp.catalog.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public class HttpHeaderFactory {
    private static final String SUCCESSFUL = "Successful";


    public static HttpHeaders getSuccessfulHeaders(HttpMethod... allowMethods) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, Arrays.toString(allowMethods));
        httpHeaders.add(HeaderKey.STATUS.getHeaderKeyLabel(), String.valueOf(HttpStatus.OK.value()));
        httpHeaders.add(HeaderKey.MESSAGE.getHeaderKeyLabel(), SUCCESSFUL);
        return httpHeaders;
    }
}

enum HeaderKey {
    STATUS("Status"),
    MESSAGE("Message");

    private String headerKeyLabel;

    HeaderKey(String headerKeyLabel) {
        this.headerKeyLabel = headerKeyLabel;
    }

    public String getHeaderKeyLabel() {
        return headerKeyLabel;
    }
}



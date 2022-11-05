package com.testaarosa.springRecallBookApp.uploads.application;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileThumbnailFetchResponse {
    private String contentType;
    private byte[] file;

}

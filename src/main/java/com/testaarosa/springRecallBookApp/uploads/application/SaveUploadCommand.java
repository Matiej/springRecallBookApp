package com.testaarosa.springRecallBookApp.uploads.application;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SaveUploadCommand {
    private String fileName;
    private byte[] file;
    private String contentType;

}

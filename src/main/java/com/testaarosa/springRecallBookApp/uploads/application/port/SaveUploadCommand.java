package com.testaarosa.springRecallBookApp.uploads.application.port;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Builder
@Getter
public class SaveUploadCommand {
    private String fileName;
    private byte[] file;
    private String contentType;

}

package com.testaarosa.springRecallBookApp.uploads.application.port;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SaveUploadCommand {
    String fileName;
    byte[] file;
    String contentType;
}

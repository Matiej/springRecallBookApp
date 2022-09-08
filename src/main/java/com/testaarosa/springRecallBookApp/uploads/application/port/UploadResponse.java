package com.testaarosa.springRecallBookApp.uploads.application.port;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Builder
public class UploadResponse {
    @Setter
    private String id;
    private String originFileName;
    private String serverFileName;
    private String contentType;
    private byte[] file;
    private LocalDateTime createdAt;
    private String path;
}

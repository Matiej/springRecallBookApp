package com.testaarosa.springRecallBookApp.uploads.application;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UploadResponse {
    private Long id;
    private String originFileName;
    private String serverFileName;
    private final String contentType;
    private byte[] file;
    private LocalDateTime createdAt;
    private String path;
}

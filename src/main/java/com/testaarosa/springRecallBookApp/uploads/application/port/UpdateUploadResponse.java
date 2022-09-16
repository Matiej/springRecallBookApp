package com.testaarosa.springRecallBookApp.uploads.application.port;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Getter
public class UpdateUploadResponse extends UploadResponse {
    private final boolean success;
    private final List<String> errorList;
    private final LocalDateTime lastUpdateAt;


    @Builder(builderMethodName = "UpdateUploadResponseBuilder")
    public UpdateUploadResponse(Long id, String originFileName, String serverFileName, String contentType, byte[] file,
                                LocalDateTime createdAt, String path, boolean success, List<String> errorList, LocalDateTime lastUpdateAt) {
        super(id, originFileName, serverFileName, contentType, file, createdAt, path);
        this.success = success;
        this.errorList = errorList;
        this.lastUpdateAt = lastUpdateAt;
    }
}

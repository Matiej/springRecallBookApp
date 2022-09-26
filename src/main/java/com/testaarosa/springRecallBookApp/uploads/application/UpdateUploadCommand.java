package com.testaarosa.springRecallBookApp.uploads.application;

import lombok.Data;

@Data
public class UpdateUploadCommand extends SaveUploadCommand {
    private final Long uploadId;


    public UpdateUploadCommand(Long uploadId, String fileName, byte[] file, String contentType) {
        super(fileName, file, contentType);
        this.uploadId = uploadId;
    }

    public SaveUploadCommand toSaveCommand() {
        return SaveUploadCommand.builder()
                .fileName(getFileName())
                .file(getFile())
                .contentType(getContentType())
                .build();

    }
}

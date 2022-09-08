package com.testaarosa.springRecallBookApp.uploads.infrastructure;

import com.testaarosa.springRecallBookApp.uploads.application.port.SaveUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.port.UploadResponse;
import com.testaarosa.springRecallBookApp.uploads.domain.Upload;
import com.testaarosa.springRecallBookApp.uploads.domain.UploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UploadRepositoryFacade implements UploadRepository {
    private final MemoryUploadRepository memoryUploadRepository;
    private final ServerUploadRepository serverUploadRepository;

    @Override
    public UploadResponse saveUpload(SaveUploadCommand command) {
        UploadResponse uploadResponse = serverUploadRepository.save(command);
        Upload upload = Upload.builder()
                .fileName(uploadResponse.getOriginFileName())
                .serverFileName(uploadResponse.getServerFileName())
                .contentType(uploadResponse.getContentType())
                .createdAt(uploadResponse.getCreatedAt())
                .path(uploadResponse.getPath())
                .build();
        Upload savedUpload = memoryUploadRepository.save(upload);
        uploadResponse.setId(savedUpload.getId());
        return uploadResponse;
    }

    //todo konwerter w klasie uplaad i uploadResponse <=>
    public Optional<UploadResponse> getUploadById(String id) {
        Optional<Upload> uploadById = memoryUploadRepository.getUploadById(id);
        return uploadById
                .map(upload -> {
                    byte[] fileByPath = serverUploadRepository.getFileByPath(upload.getPath());
                    return UploadResponse
                            .builder()
                            .id(upload.getId())
                            .originFileName(upload.getFileName())
                            .serverFileName(upload.getServerFileName())
                            .contentType(upload.getContentType())
                            .file(fileByPath)
                            .createdAt(upload.getCreatedAt())
                            .path(upload.getPath())
                            .build();
                });
    }

    @Override
    public void removeCoverById(String bookCoverId) {
        memoryUploadRepository.getUploadById(bookCoverId)
                .ifPresent(upload -> {
                    serverUploadRepository.removeFileByPath(upload.getPath());
                    memoryUploadRepository.removeUploadById(bookCoverId);
                });
    }

}

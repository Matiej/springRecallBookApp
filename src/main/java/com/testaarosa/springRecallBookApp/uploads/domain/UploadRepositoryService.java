package com.testaarosa.springRecallBookApp.uploads.domain;

import com.testaarosa.springRecallBookApp.uploads.application.port.SaveUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.port.UploadResponse;
import com.testaarosa.springRecallBookApp.uploads.dataBase.UploadJpaRepository;
import com.testaarosa.springRecallBookApp.uploads.infrastructure.ServerUploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UploadRepositoryService implements UploadRepository{
    private final UploadJpaRepository uploadJpaRepository;
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
        Upload savedUpload = uploadJpaRepository.save(upload);
        uploadResponse.setId(savedUpload.getId());
        return uploadResponse;
    }

    //todo konwerter w klasie uplaad i uploadResponse <=>
    public Optional<UploadResponse> getUploadById(Long id) {
        Optional<Upload> uploadById = uploadJpaRepository.findById(id);
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
    public void removeCoverById(Long bookCoverId) {
        uploadJpaRepository.findById(bookCoverId)
                .ifPresent(upload -> {
                    serverUploadRepository.removeFileByPath(upload.getPath());
                    uploadJpaRepository.deleteById(bookCoverId);
                });
    }
}

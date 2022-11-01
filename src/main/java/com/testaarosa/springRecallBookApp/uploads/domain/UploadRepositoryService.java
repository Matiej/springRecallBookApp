package com.testaarosa.springRecallBookApp.uploads.domain;

import com.testaarosa.springRecallBookApp.uploads.application.SaveUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.UpdateUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.UpdateUploadResponse;
import com.testaarosa.springRecallBookApp.uploads.application.UploadResponse;
import com.testaarosa.springRecallBookApp.uploads.dataBase.UploadJpaRepository;
import com.testaarosa.springRecallBookApp.uploads.infrastructure.ServerUploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
                .path(uploadResponse.getPath())
                .build();
        Upload savedUpload = uploadJpaRepository.save(upload);
        uploadResponse.setId(savedUpload.getId());
        return uploadResponse;
    }

    public Optional<UploadResponse> getUploadById(Long id) {
        Optional<Upload> uploadById = uploadJpaRepository.findById(id);
        return uploadById
                .map(upload -> {
                    byte[] fileByPath = serverUploadRepository.getFileByPath(upload.getPath());
                    if(fileByPath == null) {
                        throw new IllegalArgumentException("Can't find file for uploadID: " + id);
                    }
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

    @Override
    public UpdateUploadResponse updateUpload(UpdateUploadCommand command) {
        List<String> errorList = new ArrayList<>();
        Optional<Upload> optionalUpload = uploadJpaRepository.findById(command.getUploadId());
        byte[] file = null;
        if (optionalUpload.isEmpty()) {
            errorList.add("Can't find upload with ID: " + command.getUploadId());
        } else {
            String filePath = optionalUpload.get().getPath();
            file = serverUploadRepository.getFileByPath(filePath);
            if(file == null) {
                errorList.add("Can't find file in path: " + filePath);
            }
        }
        if(errorList.isEmpty()) {
            UploadResponse serverResponse = serverUploadRepository.save(command.toSaveCommand());
            Upload upload = optionalUpload.get();
            upload.updateFields(serverResponse);
            uploadJpaRepository.save(upload);
            return UpdateUploadResponse.UpdateUploadResponseBuilder()
                    .id(upload.getId())
                    .originFileName(serverResponse.getOriginFileName())
                    .serverFileName(serverResponse.getServerFileName())
                    .contentType(serverResponse.getContentType())
                    .file(serverResponse.getFile())
                    .createdAt(upload.getCreatedAt())
                    .success(true)
                    .lastUpdateAt(upload.getLastUpdatedAt())
                    .build();
        }
        return UpdateUploadResponse.UpdateUploadResponseBuilder()
                .success(false)
                .errorList(errorList)
                .build();
    }
}

package com.testaarosa.springRecallBookApp.uploads.application;

import com.testaarosa.springRecallBookApp.uploads.dataBase.UploadJpaRepository;
import com.testaarosa.springRecallBookApp.uploads.domain.Upload;
import com.testaarosa.springRecallBookApp.uploads.infrastructure.ServerUploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UploadRepositoryService implements UploadRepository {
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
                .thumbnailUri(command.getThumbnailUri())
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
                    return UploadResponse
                            .builder()
                            .id(upload.getId())
                            .originFileName(upload.getFileName())
                            .serverFileName(upload.getServerFileName())
                            .contentType(upload.getContentType())
                            .file(fileByPath)
                            .createdAt(upload.getCreatedAt())
                            .path(upload.getPath())
                            .thumbnailUri(upload.getThumbnailUri())
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
        if (optionalUpload.isEmpty()) {
            errorList.add("Can't find upload with ID: " + command.getUploadId());
        }
        if (errorList.isEmpty()) {
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
                    .thumbnailUri(command.getThumbnailUri())
                    .build();
        }
        return UpdateUploadResponse.UpdateUploadResponseBuilder()
                .success(false)
                .errorList(errorList)
                .build();
    }


    @Override
    public List<UploadResponse> getAllUploadsWithFileUri() {
        return uploadJpaRepository.getAllUploadsWithFileUri().stream()
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
                            .thumbnailUri(upload.getThumbnailUri())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void cleanUselessCoverFiles() {
        List<Upload> allUploadsWithFileUri = uploadJpaRepository.findAll();
        serverUploadRepository.deleteUselessCoverFiles(allUploadsWithFileUri);
    }
}

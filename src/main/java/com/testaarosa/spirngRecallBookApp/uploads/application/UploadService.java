package com.testaarosa.spirngRecallBookApp.uploads.application;

import com.testaarosa.spirngRecallBookApp.uploads.application.port.SaveUploadCommand;
import com.testaarosa.spirngRecallBookApp.uploads.application.port.UploadUseCase;
import com.testaarosa.spirngRecallBookApp.uploads.domain.Upload;
import com.testaarosa.spirngRecallBookApp.uploads.infrastructure.ServerUploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
class UploadService implements UploadUseCase {
    private final ServerUploadRepository repository;


    @Override
    public Upload save(SaveUploadCommand command) {
        return repository.saveUploadOnServer(Upload.builder()
                .fileName(command.getFileName())
                .content(command.getContentType())
                .file(command.getFile())
                .createdAt(LocalDateTime.now())
                .build());
    }
}

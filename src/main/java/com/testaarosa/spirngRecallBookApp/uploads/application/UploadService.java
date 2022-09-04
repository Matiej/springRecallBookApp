package com.testaarosa.spirngRecallBookApp.uploads.application;

import com.testaarosa.spirngRecallBookApp.uploads.application.port.SaveUploadCommand;
import com.testaarosa.spirngRecallBookApp.uploads.application.port.UploadUseCase;
import com.testaarosa.spirngRecallBookApp.uploads.domain.Upload;
import com.testaarosa.spirngRecallBookApp.uploads.infrastructure.ServerUploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UploadService implements UploadUseCase {
    private final ServerUploadRepository repository;


    @Override
    public Upload save(SaveUploadCommand command) {
        return null;
    }
}

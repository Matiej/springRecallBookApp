package com.testaarosa.spirngRecallBookApp.uploads.application;

import com.testaarosa.spirngRecallBookApp.uploads.application.port.SaveUploadCommand;
import com.testaarosa.spirngRecallBookApp.uploads.application.port.UploadResponse;
import com.testaarosa.spirngRecallBookApp.uploads.application.port.UploadUseCase;
import com.testaarosa.spirngRecallBookApp.uploads.domain.Upload;
import com.testaarosa.spirngRecallBookApp.uploads.domain.UploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class UploadService implements UploadUseCase {
    private final UploadRepository uploadRepository;

    @Override
    public UploadResponse save(SaveUploadCommand command) {
        return uploadRepository.saveUpload(command);
    }

    @Override
    public Optional<UploadResponse> getCoverUploadById(String id) {
        return uploadRepository.getUploadById(id);
    }

    @Override
    public void removeCoverById(String bookCoverId) {
        uploadRepository.removeCoverById(bookCoverId);
    }
}

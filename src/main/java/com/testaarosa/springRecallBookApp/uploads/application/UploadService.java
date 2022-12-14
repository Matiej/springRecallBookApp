package com.testaarosa.springRecallBookApp.uploads.application;

import com.testaarosa.springRecallBookApp.uploads.application.port.UploadUseCase;
import com.testaarosa.springRecallBookApp.uploads.domain.UploadRepository;
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
    public Optional<UploadResponse> getCoverUploadById(Long id) {
        return uploadRepository.getUploadById(id);
    }

    @Override
    public void removeCoverById(Long bookCoverId) {
        uploadRepository.removeCoverById(bookCoverId);
    }

    @Override
    public UpdateUploadResponse updateById(UpdateUploadCommand updateUploadCommand) {
        return uploadRepository.updateUpload(updateUploadCommand);

    }
}

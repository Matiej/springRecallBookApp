package com.testaarosa.spirngRecallBookApp.uploads.application.port;

import com.testaarosa.spirngRecallBookApp.uploads.domain.Upload;

import java.util.Optional;

public interface UploadUseCase {
    UploadResponse save(SaveUploadCommand command);
    Optional<UploadResponse> getCoverUploadById(String id);


    void removeCoverById(String bookCoverId);
}

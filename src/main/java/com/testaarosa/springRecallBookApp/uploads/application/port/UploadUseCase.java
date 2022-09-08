package com.testaarosa.springRecallBookApp.uploads.application.port;

import java.util.Optional;

public interface UploadUseCase {
    UploadResponse save(SaveUploadCommand command);
    Optional<UploadResponse> getCoverUploadById(String id);


    void removeCoverById(String bookCoverId);
}

package com.testaarosa.springRecallBookApp.uploads.application.port;

import com.testaarosa.springRecallBookApp.uploads.application.SaveUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.UpdateUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.UpdateUploadResponse;
import com.testaarosa.springRecallBookApp.uploads.application.UploadResponse;

import java.util.Optional;

public interface UploadUseCase {
    UploadResponse save(SaveUploadCommand command);
    Optional<UploadResponse> getCoverUploadById(Long id);

    void removeCoverById(Long bookCoverId);

    UpdateUploadResponse updateById(UpdateUploadCommand updateUploadCommand);
}

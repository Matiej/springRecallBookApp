package com.testaarosa.springRecallBookApp.uploads.domain;

import com.testaarosa.springRecallBookApp.uploads.application.SaveUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.UpdateUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.UpdateUploadResponse;
import com.testaarosa.springRecallBookApp.uploads.application.UploadResponse;

import java.util.Optional;

public interface UploadRepository {

    UploadResponse saveUpload(SaveUploadCommand command);

    Optional<UploadResponse> getUploadById(Long id);

    void removeCoverById(Long bookCoverId);

    UpdateUploadResponse updateUpload(UpdateUploadCommand updateUploadCommand);
}

package com.testaarosa.springRecallBookApp.uploads.domain;

import com.testaarosa.springRecallBookApp.uploads.application.port.SaveUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.port.UploadResponse;

import java.util.Optional;

public interface UploadRepository {

    UploadResponse saveUpload(SaveUploadCommand command);

    Optional<UploadResponse> getUploadById(Long id);

    void removeCoverById(Long bookCoverId);
}

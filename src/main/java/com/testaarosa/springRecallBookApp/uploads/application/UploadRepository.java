package com.testaarosa.springRecallBookApp.uploads.application;

import com.testaarosa.springRecallBookApp.uploads.application.*;

import java.util.List;
import java.util.Optional;

public interface UploadRepository {

    UploadResponse saveUpload(SaveUploadCommand command);

    Optional<UploadResponse> getUploadById(Long id);

    void removeCoverById(Long bookCoverId);

    UpdateUploadResponse updateUpload(UpdateUploadCommand updateUploadCommand);

    List<UploadResponse> getAllUploadsWithFileUri();

    void cleanUselessCoverFiles();

}

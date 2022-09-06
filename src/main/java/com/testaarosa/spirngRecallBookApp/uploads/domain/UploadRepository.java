package com.testaarosa.spirngRecallBookApp.uploads.domain;

import com.testaarosa.spirngRecallBookApp.uploads.application.port.SaveUploadCommand;
import com.testaarosa.spirngRecallBookApp.uploads.application.port.UploadResponse;

import java.util.Optional;

public interface UploadRepository {

    UploadResponse saveUpload(SaveUploadCommand command);

    Optional<UploadResponse> getUploadById(String id);
}

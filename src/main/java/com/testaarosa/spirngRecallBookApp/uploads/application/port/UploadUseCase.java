package com.testaarosa.spirngRecallBookApp.uploads.application.port;

import com.testaarosa.spirngRecallBookApp.uploads.domain.Upload;

public interface UploadUseCase {
    Upload save(SaveUploadCommand command);

}

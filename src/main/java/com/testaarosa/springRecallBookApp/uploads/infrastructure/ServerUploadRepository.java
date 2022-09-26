package com.testaarosa.springRecallBookApp.uploads.infrastructure;

import com.testaarosa.springRecallBookApp.uploads.application.SaveUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.UploadResponse;

public interface ServerUploadRepository {
    UploadResponse save(SaveUploadCommand command);
    byte[] getFileByPath(String path);
    void removeFileByPath(String path);
}

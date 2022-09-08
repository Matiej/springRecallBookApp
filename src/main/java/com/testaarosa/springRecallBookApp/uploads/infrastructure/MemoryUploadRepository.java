package com.testaarosa.springRecallBookApp.uploads.infrastructure;

import com.testaarosa.springRecallBookApp.uploads.domain.Upload;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class MemoryUploadRepository {
    private final Map<String, Upload> tmpStorage = new ConcurrentHashMap<>();

    Upload save(Upload upload) {
        String id = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
        upload.setId(id);
        tmpStorage.put(id, upload);
        return upload;
    }

    Optional<Upload> getUploadById(String id) {
        return Optional.ofNullable(tmpStorage.get(id));
    }

    public void removeUploadById(String bookCoverId) {
        tmpStorage.remove(bookCoverId);
    }
}

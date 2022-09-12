package com.testaarosa.springRecallBookApp.uploads.dataBase;

import com.testaarosa.springRecallBookApp.uploads.domain.Upload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadJpaRepository extends JpaRepository<Upload, Long> {
}

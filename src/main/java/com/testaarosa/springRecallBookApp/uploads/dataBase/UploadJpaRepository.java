package com.testaarosa.springRecallBookApp.uploads.dataBase;

import com.testaarosa.springRecallBookApp.uploads.domain.Upload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadJpaRepository extends JpaRepository<Upload, Long> {

    @Query("SELECT u FROM Upload u WHERE u.thumbnailUri IS NOT NULL")
    List<Upload> getAllUploadsWithFileUri();
}

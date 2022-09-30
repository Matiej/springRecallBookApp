package com.testaarosa.springRecallBookApp.uploads.domain;

import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import com.testaarosa.springRecallBookApp.uploads.application.UploadResponse;
import lombok.*;

import javax.persistence.Entity;
import java.util.Objects;

@Builder
@Getter
@Entity
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Upload extends BaseEntity {
    private String fileName;
    private String serverFileName;
    private String contentType;
    private String path;

    public void updateFields(UploadResponse uploadResponse) {
        setFileName(uploadResponse.getOriginFileName());
        setServerFileName(uploadResponse.getServerFileName());
        setContentType(uploadResponse.getContentType());
        setPath(uploadResponse.getPath());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Upload upload = (Upload) o;
        return Objects.equals(fileName, upload.fileName) && Objects.equals(serverFileName, upload.serverFileName) && Objects.equals(contentType, upload.contentType) && Objects.equals(path, upload.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileName, serverFileName, contentType, path);
    }
}

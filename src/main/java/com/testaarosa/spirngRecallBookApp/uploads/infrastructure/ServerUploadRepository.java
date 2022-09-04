package com.testaarosa.spirngRecallBookApp.uploads.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class ServerUploadRepository {
    @Value("${cover.picture.path=}")
    private String UPLOAD_PATH;



}

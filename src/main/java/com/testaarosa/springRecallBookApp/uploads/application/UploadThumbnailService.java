package com.testaarosa.springRecallBookApp.uploads.application;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@Service
class UploadThumbnailService {

    public FileThumbnailFetchResponse getFileFromThumbnail(String thumbnail) {
        try {
            URL thumbnailURL = new URL(thumbnail);
            URLConnection urlConnection = thumbnailURL.openConnection();
            String contentType = urlConnection.getContentType();
            byte[] file = IOUtils.toByteArray(thumbnailURL);
            return FileThumbnailFetchResponse.builder()
                    .contentType(contentType)
                    .file(file)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

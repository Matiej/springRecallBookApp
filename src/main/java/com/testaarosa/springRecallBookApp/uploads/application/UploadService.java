package com.testaarosa.springRecallBookApp.uploads.application;

import com.testaarosa.springRecallBookApp.uploads.application.port.UploadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class UploadService implements UploadUseCase {
    private final UploadRepository uploadRepository;
    private final UploadThumbnailService uploadThumbnailService;

    @Override
    public UploadResponse save(SaveUploadCommand command) {
        return uploadRepository.saveUpload(command);
    }

    @Override
    public Optional<UploadResponse> getCoverUploadById(Long id) {
        Optional<UploadResponse> optionalUploadResponse = uploadRepository.getUploadById(id);
        return optionalUploadResponse
                .map(uploadResponse -> {
                    if (uploadResponse.getFile() == null && uploadResponse.getPath() == null) {
                        throw new IllegalArgumentException("Can't find file for uploadID: " + id);
                    } else if (uploadResponse.getFile() == null) {
                        return Optional.of(renewUploadOnServer(id));
                    }
                    return Optional.of(uploadResponse);
                }).orElseGet(Optional::empty);
    }

    @Override
    public void removeCoverById(Long bookCoverId) {
        uploadRepository.removeCoverById(bookCoverId);
    }

    @Override
    public UpdateUploadResponse updateById(UpdateUploadCommand updateUploadCommand) {
        return uploadRepository.updateUpload(updateUploadCommand);

    }

    @Override
    public UploadResponse renewUploadOnServer(Long uploadId) {
        return uploadRepository.getUploadById(uploadId)
                .map(uploadResponse -> {
                    FileThumbnailFetchResponse response = uploadThumbnailService.getFileFromThumbnail(uploadResponse.getThumbnailUri());
                    return (UploadResponse) uploadRepository.updateUpload(new UpdateUploadCommand(uploadResponse.getId(),
                            uploadResponse.getOriginFileName(),
                            response.getFile(),
                            response.getContentType()));
                }).orElseThrow(() -> new IllegalArgumentException("Can't find upload ID: " + uploadId));
    }
}

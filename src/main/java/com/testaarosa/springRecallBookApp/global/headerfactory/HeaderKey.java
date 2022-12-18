package com.testaarosa.springRecallBookApp.global.headerfactory;

public enum HeaderKey {
    STATUS("Status"),
    MESSAGE("Message"),
    CREATED_AT("CreatedAt"),
    UPDATED_AT("UpdatedAt"),
    SERVER_FILENAME("ServerFileName");

    private String headerKeyLabel;

    HeaderKey(String headerKeyLabel) {
        this.headerKeyLabel = headerKeyLabel;
    }

    public String getHeaderKeyLabel() {
        return headerKeyLabel;
    }
}

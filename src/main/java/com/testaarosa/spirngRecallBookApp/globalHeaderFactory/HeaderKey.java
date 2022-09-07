package com.testaarosa.spirngRecallBookApp.globalHeaderFactory;

public enum HeaderKey {
    STATUS("Status"),
    MESSAGE("Message"),
    CREATED_AT("CreatedAt"),
    SERVER_FILENAME("ServerFileName");

    private String headerKeyLabel;

    HeaderKey(String headerKeyLabel) {
        this.headerKeyLabel = headerKeyLabel;
    }

    public String getHeaderKeyLabel() {
        return headerKeyLabel;
    }
}

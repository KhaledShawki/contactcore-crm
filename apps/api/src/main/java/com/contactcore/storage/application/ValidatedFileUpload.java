// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.application;

public record ValidatedFileUpload(
        String originalFilename,
        String contentType,
        byte[] content
) {
    public long sizeBytes() {
        return content.length;
    }
}

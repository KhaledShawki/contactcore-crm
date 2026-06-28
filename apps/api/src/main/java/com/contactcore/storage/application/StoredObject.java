// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.application;

public record StoredObject(
        String objectKey,
        String originalFilename,
        String contentType,
        long sizeBytes
) {}

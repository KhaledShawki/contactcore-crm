// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.api;

import java.time.Instant;

public record DocumentAttachmentResponse(
        Long id,
        Instant createdAt,
        String documentTypeCode,
        String documentTypeName,
        String originalFilename,
        String contentType,
        long sizeBytes,
        String downloadUrl
) {}

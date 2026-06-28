// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.application;

import org.springframework.core.io.Resource;

public record StoredObjectContent(
        Resource resource,
        String contentType,
        long sizeBytes
) {}

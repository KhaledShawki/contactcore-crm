// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.application;

import java.io.IOException;

public interface ObjectStorageService {
    StoredObject upload(String namespace, ValidatedFileUpload upload) throws IOException;

    StoredObjectContent download(String objectKey, String contentType, long sizeBytes) throws IOException;

    void delete(String objectKey) throws IOException;

    String publicUrl(String objectKey);
}

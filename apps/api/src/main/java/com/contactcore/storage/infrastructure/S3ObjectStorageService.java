// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.infrastructure;

import com.contactcore.storage.application.ObjectStorageService;
import com.contactcore.storage.application.StoredObject;
import com.contactcore.storage.application.StoredObjectContent;
import com.contactcore.storage.application.ValidatedFileUpload;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3ObjectStorageService implements ObjectStorageService {
    private final S3Client s3Client;
    private final S3StorageProperties properties;

    public S3ObjectStorageService(S3Client s3Client, S3StorageProperties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    @Override
    public StoredObject upload(String namespace, ValidatedFileUpload upload) throws IOException {
        String objectKey = namespace + "/" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID() + "-" + upload.originalFilename();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .contentType(upload.contentType())
                .contentLength(upload.sizeBytes())
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(upload.content()));
        return new StoredObject(objectKey, upload.originalFilename(), upload.contentType(), upload.sizeBytes());
    }

    @Override
    public StoredObjectContent download(String objectKey, String contentType, long sizeBytes) throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .build();
        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);
        return new StoredObjectContent(new ByteArrayResource(response.asByteArray()), contentType, sizeBytes);
    }

    @Override
    public void delete(String objectKey) throws IOException {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .build();
        s3Client.deleteObject(request);
    }

    @Override
    public String publicUrl(String objectKey) {
        return properties.publicEndpoint().replaceAll("/+$", "") + "/" + properties.bucket() + "/" + objectKey;
    }
}

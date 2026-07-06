// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.security;

import com.contactcore.iam.domain.IamResource;

public final class StorageIamResources {
    private StorageIamResources() {}

    public static IamResource objects(String tenantId) {
        return IamResource.contactCore(tenantId, StorageIamActions.SERVICE, "object", "*");
    }

    public static IamResource businessPartnerDocuments(String tenantId) {
        return IamResource.contactCore(tenantId, StorageIamActions.SERVICE, "object", "business-partner-document/*");
    }

    public static IamResource businessPartnerDocument(String tenantId, Long documentId) {
        return IamResource.contactCore(tenantId, StorageIamActions.SERVICE, "object", "business-partner-document/" + requiredId(documentId, "documentId"));
    }

    public static IamResource profileImages(String tenantId) {
        return IamResource.contactCore(tenantId, StorageIamActions.SERVICE, "object", "profile-image/*");
    }

    public static IamResource profileImage(String tenantId, Long userId) {
        return IamResource.contactCore(tenantId, StorageIamActions.SERVICE, "object", "profile-image/" + requiredId(userId, "userId"));
    }

    private static Long requiredId(Long value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        return value;
    }
}

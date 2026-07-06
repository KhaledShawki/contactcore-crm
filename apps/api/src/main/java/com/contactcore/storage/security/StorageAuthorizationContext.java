// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.security;

import com.contactcore.iam.evaluation.IamRequestContext;
import java.util.LinkedHashMap;
import java.util.Map;

public record StorageAuthorizationContext(
        Long businessPartnerId,
        Long documentId,
        Long userId,
        String documentTypeCode,
        String objectScope,
        String operation
) {
    public static StorageAuthorizationContext forBusinessPartnerDocuments(Long businessPartnerId, String operation) {
        return new StorageAuthorizationContext(businessPartnerId, null, null, null, "businessPartnerDocument", operation);
    }

    public static StorageAuthorizationContext forBusinessPartnerDocumentUpload(Long businessPartnerId, String documentTypeCode) {
        return new StorageAuthorizationContext(
                businessPartnerId,
                null,
                null,
                normalize(documentTypeCode),
                "businessPartnerDocument",
                "uploadDocument"
        );
    }

    public static StorageAuthorizationContext forBusinessPartnerDocument(Long documentId, String operation) {
        return new StorageAuthorizationContext(null, documentId, null, null, "businessPartnerDocument", operation);
    }

    public static StorageAuthorizationContext forProfileImage(Long userId, String operation) {
        return new StorageAuthorizationContext(null, null, userId, null, "profileImage", operation);
    }

    public IamRequestContext toRequestContext() {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "businessPartnerId", businessPartnerId);
        put(values, "documentId", documentId);
        put(values, "userId", userId);
        put(values, "documentTypeCode", documentTypeCode);
        put(values, "objectScope", objectScope);
        put(values, "operation", operation);
        return new IamRequestContext(values);
    }

    private static void put(Map<String, Object> values, String key, Object value) {
        if (value instanceof String stringValue) {
            if (!stringValue.isBlank()) {
                values.put(key, stringValue.trim());
            }
            return;
        }
        if (value != null) {
            values.put(key, value);
        }
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toUpperCase();
    }
}

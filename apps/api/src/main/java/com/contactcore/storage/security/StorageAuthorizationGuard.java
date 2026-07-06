// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.security;

import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.shared.api.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class StorageAuthorizationGuard {
    private final CurrentIamAuthorizationService authorization;
    private final ContactCoreTenantContext tenantContext;

    public StorageAuthorizationGuard(CurrentIamAuthorizationService authorization, ContactCoreTenantContext tenantContext) {
        this.authorization = authorization;
        this.tenantContext = tenantContext;
    }

    public void requireReadBusinessPartnerDocuments(Long businessPartnerId) {
        authorization.requireAllowed(
                StorageIamActions.READ_OBJECT,
                StorageIamResources.businessPartnerDocuments(tenantId()),
                StorageAuthorizationContext.forBusinessPartnerDocuments(requireId(businessPartnerId, "businessPartnerId"), "listDocuments")
                        .toRequestContext()
        );
    }

    public void requireUploadBusinessPartnerDocument(Long businessPartnerId, String documentTypeCode) {
        authorization.requireAllowed(
                StorageIamActions.UPLOAD_OBJECT,
                StorageIamResources.businessPartnerDocuments(tenantId()),
                StorageAuthorizationContext.forBusinessPartnerDocumentUpload(requireId(businessPartnerId, "businessPartnerId"), documentTypeCode)
                        .toRequestContext()
        );
    }

    public void requireDeleteBusinessPartnerDocument(Long documentId) {
        Long requiredDocumentId = requireId(documentId, "documentId");
        authorization.requireAllowed(
                StorageIamActions.DELETE_OBJECT,
                StorageIamResources.businessPartnerDocument(tenantId(), requiredDocumentId),
                StorageAuthorizationContext.forBusinessPartnerDocument(requiredDocumentId, "archiveDocument").toRequestContext()
        );
    }

    public void requireUploadProfileImage(Long userId) {
        Long requiredUserId = requireId(userId, "userId");
        authorization.requireAllowed(
                StorageIamActions.UPLOAD_OBJECT,
                StorageIamResources.profileImage(tenantId(), requiredUserId),
                StorageAuthorizationContext.forProfileImage(requiredUserId, "uploadProfileImage").toRequestContext()
        );
    }

    public void requireDownloadProfileImage(Long userId) {
        Long requiredUserId = requireId(userId, "userId");
        authorization.requireAllowed(
                StorageIamActions.DOWNLOAD_OBJECT,
                StorageIamResources.profileImage(tenantId(), requiredUserId),
                StorageAuthorizationContext.forProfileImage(requiredUserId, "downloadProfileImage").toRequestContext()
        );
    }

    private String tenantId() {
        return tenantContext.currentTenantId();
    }

    private static Long requireId(Long value, String fieldName) {
        if (value == null) {
            throw new InvalidRequestException(fieldName + " must not be null");
        }
        return value;
    }
}

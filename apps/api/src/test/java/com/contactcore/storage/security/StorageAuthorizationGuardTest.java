// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.evaluation.IamRequestContext;
import com.contactcore.shared.api.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class StorageAuthorizationGuardTest {
    private final CurrentIamAuthorizationService authorization = mock(CurrentIamAuthorizationService.class);
    private final ContactCoreTenantContext tenantContext = mock(ContactCoreTenantContext.class);
    private final StorageAuthorizationGuard guard = new StorageAuthorizationGuard(authorization, tenantContext);

    @BeforeEach
    void setUp() {
        when(tenantContext.currentTenantId()).thenReturn("tenant-1");
    }

    @Test
    void requiresReadBusinessPartnerDocumentsWithBusinessPartnerContext() {
        guard.requireReadBusinessPartnerDocuments(42L);

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(StorageIamActions.READ_OBJECT),
                eq(IamResource.of("contactcore:tenant-1:storage:object/business-partner-document/*")),
                context.capture()
        );
        assertThat(context.getValue().get("businessPartnerId")).isEqualTo(42L);
        assertThat(context.getValue().get("objectScope")).isEqualTo("businessPartnerDocument");
        assertThat(context.getValue().get("operation")).isEqualTo("listDocuments");
    }

    @Test
    void requiresUploadBusinessPartnerDocumentWithDocumentTypeContext() {
        guard.requireUploadBusinessPartnerDocument(42L, "general");

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(StorageIamActions.UPLOAD_OBJECT),
                eq(IamResource.of("contactcore:tenant-1:storage:object/business-partner-document/*")),
                context.capture()
        );
        assertThat(context.getValue().get("businessPartnerId")).isEqualTo(42L);
        assertThat(context.getValue().get("documentTypeCode")).isEqualTo("GENERAL");
        assertThat(context.getValue().get("operation")).isEqualTo("uploadDocument");
    }

    @Test
    void requiresDeleteBusinessPartnerDocumentWithSpecificDocumentResource() {
        guard.requireDeleteBusinessPartnerDocument(99L);

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(StorageIamActions.DELETE_OBJECT),
                eq(IamResource.of("contactcore:tenant-1:storage:object/business-partner-document/99")),
                context.capture()
        );
        assertThat(context.getValue().get("documentId")).isEqualTo(99L);
        assertThat(context.getValue().get("operation")).isEqualTo("archiveDocument");
    }

    @Test
    void requiresProfileImageUploadWithUserScopedResource() {
        guard.requireUploadProfileImage(7L);

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(StorageIamActions.UPLOAD_OBJECT),
                eq(IamResource.of("contactcore:tenant-1:storage:object/profile-image/7")),
                context.capture()
        );
        assertThat(context.getValue().get("userId")).isEqualTo(7L);
        assertThat(context.getValue().get("operation")).isEqualTo("uploadProfileImage");
    }

    @Test
    void requiresProfileImageDownloadWithUserScopedResource() {
        guard.requireDownloadProfileImage(7L);

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(StorageIamActions.DOWNLOAD_OBJECT),
                eq(IamResource.of("contactcore:tenant-1:storage:object/profile-image/7")),
                context.capture()
        );
        assertThat(context.getValue().get("userId")).isEqualTo(7L);
        assertThat(context.getValue().get("operation")).isEqualTo("downloadProfileImage");
    }

    @Test
    void rejectsMissingIds() {
        assertThatThrownBy(() -> guard.requireReadBusinessPartnerDocuments(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("businessPartnerId");
        assertThatThrownBy(() -> guard.requireDeleteBusinessPartnerDocument(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("documentId");
        assertThatThrownBy(() -> guard.requireUploadProfileImage(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("userId");
        assertThatThrownBy(() -> guard.requireDownloadProfileImage(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("userId");
    }
}

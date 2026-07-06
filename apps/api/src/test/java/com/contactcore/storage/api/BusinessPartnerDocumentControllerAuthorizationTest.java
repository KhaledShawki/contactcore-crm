// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.contactcore.crm.security.CrmAuthorizationGuard;
import com.contactcore.iam.application.IamAccessDeniedException;
import com.contactcore.storage.application.DocumentAttachmentService;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.web.multipart.MultipartFile;

class BusinessPartnerDocumentControllerAuthorizationTest {
    private final DocumentAttachmentService service = mock(DocumentAttachmentService.class);
    private final CrmAuthorizationGuard authorization = mock(CrmAuthorizationGuard.class);
    private final BusinessPartnerDocumentController controller = new BusinessPartnerDocumentController(service, authorization);

    @Test
    void listRequiresBusinessPartnerReadPermission() {
        controller.list(42L);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireReadBusinessPartner(42L);
        inOrder.verify(service).listBusinessPartnerDocuments(42L);
    }

    @Test
    void uploadRequiresBusinessPartnerUpdatePermission() throws Exception {
        MultipartFile file = mock(MultipartFile.class);

        controller.upload(42L, "GENERAL", file);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireManageBusinessPartnerDocuments(42L, "uploadDocument");
        inOrder.verify(service).uploadBusinessPartnerDocument(42L, "GENERAL", file);
    }

    @Test
    void archiveRequiresDocumentManagementPermissionBeforeServiceCall() {
        controller.archive(99L);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireManageBusinessPartnerDocuments("archiveDocument");
        inOrder.verify(service).archiveBusinessPartnerDocument(99L);
    }

    @Test
    void skipsUploadWhenAuthorizationFails() throws Exception {
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        MultipartFile file = mock(MultipartFile.class);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireManageBusinessPartnerDocuments(42L, "uploadDocument");

        assertThatThrownBy(() -> controller.upload(42L, "GENERAL", file)).isSameAs(denied);

        verify(service, never()).uploadBusinessPartnerDocument(42L, "GENERAL", file);
    }
}

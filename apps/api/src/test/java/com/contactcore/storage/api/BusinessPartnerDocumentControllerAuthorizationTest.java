// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.crm.security.CrmAuthorizationGuard;
import com.contactcore.iam.application.IamAccessDeniedException;
import com.contactcore.storage.application.BusinessPartnerDocumentAccessService;
import com.contactcore.storage.application.DocumentAttachmentService;
import com.contactcore.storage.security.StorageAuthorizationGuard;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.web.multipart.MultipartFile;

class BusinessPartnerDocumentControllerAuthorizationTest {
    private final DocumentAttachmentService service = mock(DocumentAttachmentService.class);
    private final BusinessPartnerDocumentAccessService accessService = mock(BusinessPartnerDocumentAccessService.class);
    private final CrmAuthorizationGuard crmAuthorization = mock(CrmAuthorizationGuard.class);
    private final StorageAuthorizationGuard storageAuthorization = mock(StorageAuthorizationGuard.class);
    private final BusinessPartnerDocumentController controller = new BusinessPartnerDocumentController(
            service,
            accessService,
            crmAuthorization,
            storageAuthorization
    );

    @Test
    void listRequiresBusinessPartnerReadAndStorageReadBeforeServiceCall() {
        controller.list(42L);

        InOrder inOrder = inOrder(crmAuthorization, storageAuthorization, service);
        inOrder.verify(crmAuthorization).requireReadBusinessPartner(42L);
        inOrder.verify(storageAuthorization).requireReadBusinessPartnerDocuments(42L);
        inOrder.verify(service).listBusinessPartnerDocuments(42L);
    }

    @Test
    void uploadRequiresBusinessPartnerUpdateAndStorageUploadBeforeServiceCall() throws Exception {
        MultipartFile file = mock(MultipartFile.class);

        controller.upload(42L, "GENERAL", file);

        InOrder inOrder = inOrder(crmAuthorization, storageAuthorization, service);
        inOrder.verify(crmAuthorization).requireManageBusinessPartnerDocuments(42L, "uploadDocument");
        inOrder.verify(storageAuthorization).requireUploadBusinessPartnerDocument(42L, "GENERAL");
        inOrder.verify(service).uploadBusinessPartnerDocument(42L, "GENERAL", file);
    }

    @Test
    void archiveRequiresStorageDeleteThenDocumentOwnerResolutionThenBusinessPartnerUpdateBeforeServiceCall() {
        when(accessService.requireActiveBusinessPartnerId(99L)).thenReturn(42L);

        controller.archive(99L);

        InOrder inOrder = inOrder(storageAuthorization, accessService, crmAuthorization, service);
        inOrder.verify(storageAuthorization).requireDeleteBusinessPartnerDocument(99L);
        inOrder.verify(accessService).requireActiveBusinessPartnerId(99L);
        inOrder.verify(crmAuthorization).requireManageBusinessPartnerDocuments(42L, "archiveDocument");
        inOrder.verify(service).archiveBusinessPartnerDocument(99L);
    }

    @Test
    void skipsUploadWhenCrmAuthorizationFails() throws Exception {
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        MultipartFile file = mock(MultipartFile.class);
        org.mockito.Mockito.doThrow(denied).when(crmAuthorization).requireManageBusinessPartnerDocuments(42L, "uploadDocument");

        assertThatThrownBy(() -> controller.upload(42L, "GENERAL", file)).isSameAs(denied);

        verify(storageAuthorization, never()).requireUploadBusinessPartnerDocument(42L, "GENERAL");
        verify(service, never()).uploadBusinessPartnerDocument(42L, "GENERAL", file);
    }

    @Test
    void skipsUploadWhenStorageAuthorizationFails() throws Exception {
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        MultipartFile file = mock(MultipartFile.class);
        org.mockito.Mockito.doThrow(denied).when(storageAuthorization).requireUploadBusinessPartnerDocument(42L, "GENERAL");

        assertThatThrownBy(() -> controller.upload(42L, "GENERAL", file)).isSameAs(denied);

        verify(service, never()).uploadBusinessPartnerDocument(42L, "GENERAL", file);
    }

    @Test
    void skipsArchiveWhenStorageAuthorizationFails() {
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(storageAuthorization).requireDeleteBusinessPartnerDocument(99L);

        assertThatThrownBy(() -> controller.archive(99L)).isSameAs(denied);

        verify(accessService, never()).requireActiveBusinessPartnerId(99L);
        verify(crmAuthorization, never()).requireManageBusinessPartnerDocuments(42L, "archiveDocument");
        verify(service, never()).archiveBusinessPartnerDocument(99L);
    }
}

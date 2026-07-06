// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.contactcore.crm.application.ContactPersonService;
import com.contactcore.crm.security.CrmAuthorizationGuard;
import com.contactcore.iam.application.IamAccessDeniedException;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class ContactPersonControllerAuthorizationTest {
    private final ContactPersonService service = mock(ContactPersonService.class);
    private final CrmAuthorizationGuard authorization = mock(CrmAuthorizationGuard.class);
    private final ContactPersonController controller = new ContactPersonController(service, authorization);

    @Test
    void listRequiresBusinessPartnerReadPermission() {
        controller.list(42L);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireReadBusinessPartner(42L);
        inOrder.verify(service).list(42L);
    }

    @Test
    void createRequiresBusinessPartnerUpdatePermission() {
        ContactPersonWriteRequest request = request();

        controller.create(42L, request);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireUpdateBusinessPartner(42L);
        inOrder.verify(service).create(42L, request);
    }

    @Test
    void updateRequiresBusinessPartnerUpdatePermission() {
        ContactPersonWriteRequest request = request();

        controller.update(42L, 99L, request);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireUpdateBusinessPartner(42L);
        inOrder.verify(service).update(42L, 99L, request);
    }

    @Test
    void skipsContactServiceWhenAuthorizationFails() {
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireUpdateBusinessPartner(42L);

        assertThatThrownBy(() -> controller.archive(42L, 99L)).isSameAs(denied);

        verify(service, never()).archive(42L, 99L);
    }

    private ContactPersonWriteRequest request() {
        return new ContactPersonWriteRequest(
                "Jane", "Doe", "Manager", "jane@example.com", null, null, null, false, null
        );
    }
}

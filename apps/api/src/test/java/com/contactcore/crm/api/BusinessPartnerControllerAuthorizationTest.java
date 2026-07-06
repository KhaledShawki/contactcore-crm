// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.crm.application.BusinessPartnerService;
import com.contactcore.crm.security.BusinessPartnerAuthorizationContext;
import com.contactcore.crm.security.CrmAuthorizationGuard;
import com.contactcore.iam.application.IamAccessDeniedException;
import com.contactcore.shared.api.PageResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class BusinessPartnerControllerAuthorizationTest {
    private final BusinessPartnerService service = mock(BusinessPartnerService.class);
    private final CrmAuthorizationGuard authorization = mock(CrmAuthorizationGuard.class);
    private final BusinessPartnerController controller = new BusinessPartnerController(service, authorization);

    @Test
    void authorizesBeforeSearchingBusinessPartners() {
        when(service.search(any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(new PageResponse<>(List.of(), 0, 20, 0, 0));

        controller.search("CUSTOMER", "Acme", 0, 20, "updated_desc");

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireListBusinessPartners(any(BusinessPartnerAuthorizationContext.class));
        inOrder.verify(service).search("CUSTOMER", "Acme", 0, 20, "updated_desc");
    }

    @Test
    void skipsSearchWhenAuthorizationFails() {
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireListBusinessPartners(any(BusinessPartnerAuthorizationContext.class));

        assertThatThrownBy(() -> controller.search("CUSTOMER", "", 0, 20, "updated_desc"))
                .isSameAs(denied);

        verify(service, never()).search(any(), any(), anyInt(), anyInt(), any());
    }

    @Test
    void authorizesBeforeReadingBusinessPartner() {
        controller.get(42L);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireReadBusinessPartner(42L);
        inOrder.verify(service).get(42L);
    }

    @Test
    void authorizesBeforeCreatingBusinessPartner() {
        BusinessPartnerWriteRequest request = request();

        controller.create(request);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireCreateBusinessPartner(any(BusinessPartnerAuthorizationContext.class));
        inOrder.verify(service).create(eq(request));
    }

    @Test
    void authorizesBeforeUpdatingBusinessPartner() {
        BusinessPartnerWriteRequest request = request();

        controller.update(42L, request);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireUpdateBusinessPartner(eq(42L), any(BusinessPartnerAuthorizationContext.class));
        inOrder.verify(service).update(42L, request);
    }

    @Test
    void authorizesBeforeArchivingBusinessPartner() {
        controller.archive(42L);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireDeleteBusinessPartner(42L);
        inOrder.verify(service).archive(42L);
    }

    private BusinessPartnerWriteRequest request() {
        return new BusinessPartnerWriteRequest(
                "CUSTOMER", "ACTIVE", "C1000", "Acme AG", "info@example.com", null, null,
                null, null, null, null, null, null, null
        );
    }
}

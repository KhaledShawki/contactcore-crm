// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.contactcore.commercial.application.BusinessPartnerSalesActivityService;
import com.contactcore.commercial.security.CommercialAuthorizationGuard;
import com.contactcore.commercial.security.CommercialDocumentAuthorizationContext;
import com.contactcore.iam.application.IamAccessDeniedException;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class BusinessPartnerSalesActivityControllerAuthorizationTest {
    private final BusinessPartnerSalesActivityService service = mock(BusinessPartnerSalesActivityService.class);
    private final CommercialAuthorizationGuard authorization = mock(CommercialAuthorizationGuard.class);
    private final BusinessPartnerSalesActivityController controller = new BusinessPartnerSalesActivityController(service, authorization);

    @Test
    void authorizesBeforeLoadingSalesActivity() {
        controller.get(42L, 10);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireListDocuments(any(CommercialDocumentAuthorizationContext.class));
        inOrder.verify(service).get(42L, 10);
    }

    @Test
    void skipsQueryWhenAuthorizationFails() {
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireListDocuments(any(CommercialDocumentAuthorizationContext.class));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> controller.get(42L, 10))
                .isSameAs(denied);

        verify(service, never()).get(42L, 10);
    }
}

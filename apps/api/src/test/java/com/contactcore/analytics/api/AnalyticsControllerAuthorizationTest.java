// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.analytics.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.contactcore.analytics.application.AnalyticsService;
import com.contactcore.crm.security.BusinessPartnerAuthorizationContext;
import com.contactcore.crm.security.CrmAuthorizationGuard;
import com.contactcore.iam.application.IamAccessDeniedException;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class AnalyticsControllerAuthorizationTest {
    private final AnalyticsService service = mock(AnalyticsService.class);
    private final CrmAuthorizationGuard authorization = mock(CrmAuthorizationGuard.class);
    private final AnalyticsController controller = new AnalyticsController(service, authorization);

    @Test
    void dashboardRequiresCrmListPermissionBeforeServiceCall() {
        controller.dashboard();

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireListBusinessPartners(any(BusinessPartnerAuthorizationContext.class));
        inOrder.verify(service).dashboard();
    }

    @Test
    void crmReportRequiresCrmListPermissionBeforeServiceCall() {
        controller.crmReport();

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireListBusinessPartners(any(BusinessPartnerAuthorizationContext.class));
        inOrder.verify(service).crmReport();
    }

    @Test
    void skipsAnalyticsWhenAuthorizationFails() {
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireListBusinessPartners(any(BusinessPartnerAuthorizationContext.class));

        assertThatThrownBy(controller::crmReport).isSameAs(denied);

        verify(service, never()).crmReport();
    }
}

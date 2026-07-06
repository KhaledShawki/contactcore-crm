// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.dashboard.application.CommercialDashboardDateRange;
import com.contactcore.dashboard.application.CommercialDashboardGroupBy;
import com.contactcore.dashboard.application.CommercialDashboardQuery;
import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.evaluation.IamRequestContext;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DashboardAuthorizationGuardTest {
    private final CurrentIamAuthorizationService authorization = mock(CurrentIamAuthorizationService.class);
    private final ContactCoreTenantContext tenantContext = mock(ContactCoreTenantContext.class);
    private final DashboardAuthorizationGuard guard = new DashboardAuthorizationGuard(authorization, tenantContext);

    @BeforeEach
    void setUp() {
        when(tenantContext.currentTenantId()).thenReturn("tenant-1");
    }

    @Test
    void requiresCommercialDashboardReadWithContext() {
        CommercialDashboardQuery query = query();

        guard.requireCommercialDashboard("topCustomers", query);

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(DashboardIamActions.READ_COMMERCIAL_DASHBOARD),
                eq(IamResource.of("contactcore:tenant-1:dashboard:commercial")),
                context.capture()
        );
        assertThat(context.getValue().get("dashboard")).isEqualTo("commercial");
        assertThat(context.getValue().get("widget")).isEqualTo("topCustomers");
        assertThat(context.getValue().get("currency")).isEqualTo("CHF");
    }

    @Test
    void requiresCommercialFinancialsWithFinancialResource() {
        CommercialDashboardQuery query = query();

        guard.requireCommercialFinancials("invoiceAging", query);

        verify(authorization).requireAllowed(
                eq(DashboardIamActions.READ_COMMERCIAL_FINANCIALS),
                eq(IamResource.of("contactcore:tenant-1:dashboard:commercial/financials")),
                org.mockito.ArgumentMatchers.any(IamRequestContext.class)
        );
    }

    private static CommercialDashboardQuery query() {
        return new CommercialDashboardQuery(
                new CommercialDashboardDateRange(LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31")),
                "CHF",
                10,
                CommercialDashboardGroupBy.MONTH
        );
    }
}

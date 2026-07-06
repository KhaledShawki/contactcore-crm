// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.security;

import com.contactcore.dashboard.application.CommercialDashboardQuery;
import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import org.springframework.stereotype.Component;

@Component
public class DashboardAuthorizationGuard {
    private final CurrentIamAuthorizationService authorization;
    private final ContactCoreTenantContext tenantContext;

    public DashboardAuthorizationGuard(CurrentIamAuthorizationService authorization, ContactCoreTenantContext tenantContext) {
        this.authorization = authorization;
        this.tenantContext = tenantContext;
    }

    public void requireCommercialDashboard(String widget, CommercialDashboardQuery query) {
        authorization.requireAllowed(
                DashboardIamActions.READ_COMMERCIAL_DASHBOARD,
                DashboardIamResources.commercialDashboard(tenantId()),
                DashboardAuthorizationContext.forCommercialWidget(widget, query).toRequestContext()
        );
    }

    public void requireCommercialFinancials(String widget, CommercialDashboardQuery query) {
        authorization.requireAllowed(
                DashboardIamActions.READ_COMMERCIAL_FINANCIALS,
                DashboardIamResources.commercialFinancials(tenantId()),
                DashboardAuthorizationContext.forCommercialWidget(widget, query).toRequestContext()
        );
    }

    private String tenantId() {
        return tenantContext.currentTenantId();
    }
}

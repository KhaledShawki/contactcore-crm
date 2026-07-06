// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.security;

import com.contactcore.iam.domain.IamResource;

public final class DashboardIamResources {
    private DashboardIamResources() {}

    public static IamResource commercialDashboard(String tenantId) {
        return IamResource.of("contactcore:%s:dashboard:commercial".formatted(tenantId));
    }

    public static IamResource commercialFinancials(String tenantId) {
        return IamResource.of("contactcore:%s:dashboard:commercial/financials".formatted(tenantId));
    }
}

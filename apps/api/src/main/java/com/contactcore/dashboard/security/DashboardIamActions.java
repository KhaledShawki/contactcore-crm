// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.security;

import com.contactcore.iam.domain.IamAction;

public final class DashboardIamActions {
    public static final String SERVICE = "dashboard";

    public static final IamAction READ_COMMERCIAL_DASHBOARD = IamAction.of("dashboard:ReadCommercialDashboard");
    public static final IamAction READ_COMMERCIAL_FINANCIALS = IamAction.of("dashboard:ReadCommercialFinancials");

    private DashboardIamActions() {}
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.security;

import com.contactcore.iam.application.ProductCapabilityCatalog;
import com.contactcore.iam.evaluation.ProductCapabilityRule;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DashboardProductCapabilityCatalog implements ProductCapabilityCatalog {
    @Override
    public String service() {
        return DashboardIamActions.SERVICE;
    }

    @Override
    public List<ProductCapabilityRule> rulesForTenant(String tenantId) {
        return List.of(
                new ProductCapabilityRule(DashboardIamActions.READ_COMMERCIAL_DASHBOARD, DashboardIamResources.commercialDashboard(tenantId)),
                new ProductCapabilityRule(DashboardIamActions.READ_COMMERCIAL_FINANCIALS, DashboardIamResources.commercialFinancials(tenantId))
        );
    }
}

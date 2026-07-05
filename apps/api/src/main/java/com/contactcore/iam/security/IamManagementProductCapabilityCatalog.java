// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.security;

import com.contactcore.iam.application.ProductCapabilityCatalog;
import com.contactcore.iam.evaluation.ProductCapabilityRule;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IamManagementProductCapabilityCatalog implements ProductCapabilityCatalog {
    @Override
    public String service() {
        return IamManagementActions.SERVICE;
    }

    @Override
    public List<ProductCapabilityRule> rulesForTenant(String tenantId) {
        return List.of(
                new ProductCapabilityRule(IamManagementActions.READ_POLICY, IamManagementResources.policies(tenantId)),
                new ProductCapabilityRule(IamManagementActions.MANAGE_POLICY, IamManagementResources.policies(tenantId)),
                new ProductCapabilityRule(IamManagementActions.MANAGE_ROLE, IamManagementResources.roles(tenantId))
        );
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.security;

import com.contactcore.iam.application.ProductCapabilityCatalog;
import com.contactcore.iam.evaluation.ProductCapabilityRule;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CrmProductCapabilityCatalog implements ProductCapabilityCatalog {
    @Override
    public String service() {
        return CrmIamActions.SERVICE;
    }

    @Override
    public List<ProductCapabilityRule> rulesForTenant(String tenantId) {
        return CrmIamActions.catalog().stream()
                .map(descriptor -> new ProductCapabilityRule(descriptor.action(), CrmIamResources.businessPartners(tenantId)))
                .toList();
    }
}

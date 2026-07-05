// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.security;

import com.contactcore.iam.application.ProductCapabilityCatalog;
import com.contactcore.iam.evaluation.ProductCapabilityRule;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConnectorProductCapabilityCatalog implements ProductCapabilityCatalog {
    @Override
    public String service() {
        return ConnectorIamActions.SERVICE;
    }

    @Override
    public List<ProductCapabilityRule> rulesForTenant(String tenantId) {
        return ConnectorIamActions.catalog().stream()
                .map(descriptor -> new ProductCapabilityRule(descriptor.action(), ConnectorIamResources.instances(tenantId)))
                .toList();
    }
}

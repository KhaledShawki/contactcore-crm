// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.iam.application.ProductCapabilityCatalog;
import com.contactcore.iam.evaluation.ProductCapabilityRule;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AssistantProductCapabilityCatalog implements ProductCapabilityCatalog {
    @Override
    public String service() {
        return AssistantIamActions.SERVICE;
    }

    @Override
    public List<ProductCapabilityRule> rulesForTenant(String tenantId) {
        return List.of(
                new ProductCapabilityRule(AssistantIamActions.ASK, AssistantIamResources.sessions(tenantId)),
                new ProductCapabilityRule(AssistantIamActions.USE_COMMERCIAL_TOOLS, AssistantIamResources.commercialTools(tenantId))
        );
    }
}

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
                new ProductCapabilityRule(AssistantIamActions.READ_CONVERSATIONS, AssistantIamResources.conversations(tenantId)),
                new ProductCapabilityRule(AssistantIamActions.ARCHIVE_CONVERSATION, AssistantIamResources.conversations(tenantId)),
                new ProductCapabilityRule(AssistantIamActions.USE_CRM_TOOLS, AssistantIamResources.crmTools(tenantId)),
                new ProductCapabilityRule(AssistantIamActions.USE_COMMERCIAL_TOOLS, AssistantIamResources.commercialTools(tenantId)),
                new ProductCapabilityRule(AssistantIamActions.USE_CONNECTOR_TOOLS, AssistantIamResources.connectorTools(tenantId)),
                new ProductCapabilityRule(AssistantIamActions.USE_SCHEMA_TOOLS, AssistantIamResources.schemaTools(tenantId)),
                new ProductCapabilityRule(AssistantIamActions.USE_REPORT_TOOLS, AssistantIamResources.reportTools(tenantId))
        );
    }
}

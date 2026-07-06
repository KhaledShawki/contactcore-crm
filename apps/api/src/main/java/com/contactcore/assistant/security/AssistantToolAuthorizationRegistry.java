// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.connector.security.ConnectorIamActions;
import com.contactcore.connector.security.ConnectorIamResources;
import com.contactcore.crm.security.CrmIamActions;
import com.contactcore.crm.security.CrmIamResources;
import com.contactcore.shared.api.InvalidRequestException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AssistantToolAuthorizationRegistry {
    private final Map<String, AssistantToolAuthorizationRule> rulesByToolName;

    public AssistantToolAuthorizationRegistry() {
        this.rulesByToolName = index(List.of(
                crmListTool("crm.searchRecords"),
                crmReadTool("crm.getBusinessPartnerDetails"),
                crmListTool("crm.listLeadsWithoutContactPersons"),
                crmListTool("crm.listStaleLeads"),
                crmListTool("crm.getMarketingSourcePerformance"),
                crmListTool("crm.getRecentRecords"),
                crmListTool("crm.getStatusBreakdown"),
                crmListTool("crm.getContactCoverage"),
                crmListTool("crm.getLeadPipeline"),
                crmListTool("crm.getCrmSummary")
        ));
    }

    public AssistantToolAuthorizationRule require(String toolName) {
        AssistantToolAuthorizationRule rule = rulesByToolName.get(toolName);
        if (rule == null) {
            throw new InvalidRequestException("Assistant tool has no IAM authorization rule: " + toolName);
        }
        return rule;
    }

    public AssistantToolAuthorizationRule connectorBusinessPartnerSearchRule(String toolName) {
        return new AssistantToolAuthorizationRule(
                toolName,
                AssistantToolCategory.CONNECTOR,
                ConnectorIamActions.READ_BUSINESS_PARTNERS,
                ConnectorIamResources::businessPartners
        );
    }

    private static Map<String, AssistantToolAuthorizationRule> index(List<AssistantToolAuthorizationRule> rules) {
        Map<String, AssistantToolAuthorizationRule> indexed = new LinkedHashMap<>();
        for (AssistantToolAuthorizationRule rule : rules) {
            AssistantToolAuthorizationRule previous = indexed.putIfAbsent(rule.toolName(), rule);
            if (previous != null) {
                throw new IllegalStateException("Duplicate assistant tool authorization rule: " + rule.toolName());
            }
        }
        return Map.copyOf(indexed);
    }

    private static AssistantToolAuthorizationRule crmListTool(String toolName) {
        return new AssistantToolAuthorizationRule(
                toolName,
                AssistantToolCategory.CRM,
                CrmIamActions.LIST_BUSINESS_PARTNERS,
                CrmIamResources::businessPartners
        );
    }

    private static AssistantToolAuthorizationRule crmReadTool(String toolName) {
        return new AssistantToolAuthorizationRule(
                toolName,
                AssistantToolCategory.CRM,
                CrmIamActions.READ_BUSINESS_PARTNER,
                CrmIamResources::businessPartners
        );
    }
}

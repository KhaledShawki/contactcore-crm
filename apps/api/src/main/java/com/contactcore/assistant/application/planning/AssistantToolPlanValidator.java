// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.planning;

import com.contactcore.assistant.tool.AssistantToolCall;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AssistantToolPlanValidator {
    private static final Set<AssistantIntent> NO_TOOL_INTENTS = Set.of(
            AssistantIntent.GREETING,
            AssistantIntent.ASSISTANT_IDENTITY,
            AssistantIntent.ASSISTANT_CAPABILITIES,
            AssistantIntent.UNCLEAR_REQUEST,
            AssistantIntent.UNSUPPORTED
    );
    private static final Map<AssistantIntent, Set<String>> ALLOWED_TOOLS = new EnumMap<>(AssistantIntent.class);

    static {
        ALLOWED_TOOLS.put(AssistantIntent.GREETING, Set.of());
        ALLOWED_TOOLS.put(AssistantIntent.ASSISTANT_IDENTITY, Set.of());
        ALLOWED_TOOLS.put(AssistantIntent.ASSISTANT_CAPABILITIES, Set.of());
        ALLOWED_TOOLS.put(AssistantIntent.UNCLEAR_REQUEST, Set.of());
        ALLOWED_TOOLS.put(AssistantIntent.BUSINESS_PARTNER_EXISTENCE, Set.of("crm.searchRecords"));
        ALLOWED_TOOLS.put(AssistantIntent.BUSINESS_PARTNER_SEARCH, Set.of("crm.searchRecords"));
        ALLOWED_TOOLS.put(AssistantIntent.BUSINESS_PARTNER_DETAILS, Set.of("crm.getBusinessPartnerDetails"));
        ALLOWED_TOOLS.put(AssistantIntent.LEADS_MISSING_CONTACTS, Set.of("crm.listLeadsWithoutContactPersons"));
        ALLOWED_TOOLS.put(AssistantIntent.LEADS_NEED_FOLLOW_UP, Set.of("crm.listStaleLeads"));
        ALLOWED_TOOLS.put(AssistantIntent.MARKETING_PERFORMANCE, Set.of("crm.getMarketingSourcePerformance"));
        ALLOWED_TOOLS.put(AssistantIntent.CRM_SUMMARY, Set.of("crm.getCrmSummary", "crm.getStatusBreakdown", "crm.getContactCoverage"));
        ALLOWED_TOOLS.put(AssistantIntent.STATUS_BREAKDOWN, Set.of("crm.getStatusBreakdown"));
        ALLOWED_TOOLS.put(AssistantIntent.CONTACT_COVERAGE, Set.of("crm.getContactCoverage"));
        ALLOWED_TOOLS.put(AssistantIntent.RECENT_RECORDS, Set.of("crm.getRecentRecords"));
        ALLOWED_TOOLS.put(AssistantIntent.LEAD_PIPELINE, Set.of("crm.getLeadPipeline"));
        ALLOWED_TOOLS.put(AssistantIntent.UNSUPPORTED, Set.of());
    }

    public void validate(AssistantIntent intent, List<AssistantToolCall> calls) {
        List<AssistantToolCall> safeCalls = calls == null ? List.of() : calls;

        if (NO_TOOL_INTENTS.contains(intent)) {
            if (!safeCalls.isEmpty()) {
                throw new AssistantPlanValidationException("This assistant intent must not execute CRM tools.");
            }
            return;
        }

        if (safeCalls.isEmpty()) {
            throw new AssistantPlanValidationException("Assistant could not build a safe CRM tool plan for this request.");
        }

        Set<String> allowed = ALLOWED_TOOLS.getOrDefault(intent, Set.of());
        for (AssistantToolCall call : safeCalls) {
            if (!allowed.contains(call.toolName())) {
                throw new AssistantPlanValidationException(
                        "Unsafe assistant tool plan: " + call.toolName() + " is not valid for " + intent + "."
                );
            }
        }
    }
}

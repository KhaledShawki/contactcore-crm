// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.retrieval;

import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.tool.AssistantToolCall;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public record AssistantPlan(
        AssistantRetrievalType retrievalType,
        AssistantIntent intent,
        String normalizedQuery,
        String searchTerm,
        String kindCode,
        String source,
        int maxResults,
        List<AssistantToolCall> toolCalls,
        String userIntent
) {
    public AssistantPlan {
        intent = intent == null ? AssistantIntent.CRM_SUMMARY : intent;
        normalizedQuery = normalizedQuery == null ? "" : normalizedQuery.trim();
        searchTerm = searchTerm == null ? "" : searchTerm.trim();
        kindCode = kindCode == null ? "" : kindCode.trim().toUpperCase(Locale.ROOT);
        source = source == null ? "" : source.trim();
        toolCalls = toolCalls == null ? List.of() : List.copyOf(toolCalls);
        userIntent = userIntent == null || userIntent.isBlank() ? intent.name() : userIntent.trim();
    }

    public AssistantPlan(AssistantRetrievalType retrievalType, String normalizedQuery, int maxResults) {
        this(
                retrievalType,
                intentFromType(retrievalType),
                normalizedQuery,
                normalizedQuery,
                "",
                "",
                maxResults,
                defaultToolCalls(retrievalType, normalizedQuery),
                retrievalType.name()
        );
    }

    public AssistantPlan(AssistantRetrievalType retrievalType,
                         String normalizedQuery,
                         int maxResults,
                         List<AssistantToolCall> toolCalls,
                         String userIntent) {
        this(
                retrievalType,
                intentFromType(retrievalType),
                normalizedQuery,
                "",
                "",
                "",
                maxResults,
                toolCalls,
                userIntent
        );
    }

    private static AssistantIntent intentFromType(AssistantRetrievalType type) {
        return switch (type) {
            case CRM_SEARCH -> AssistantIntent.BUSINESS_PARTNER_SEARCH;
            case BUSINESS_PARTNER_DETAILS -> AssistantIntent.BUSINESS_PARTNER_DETAILS;
            case STALE_LEADS -> AssistantIntent.LEADS_NEED_FOLLOW_UP;
            case LEADS_WITHOUT_CONTACTS -> AssistantIntent.LEADS_MISSING_CONTACTS;
            case MARKETING_SOURCE_ANALYTICS -> AssistantIntent.MARKETING_PERFORMANCE;
            case CONTACT_COVERAGE -> AssistantIntent.CONTACT_COVERAGE;
            case RECENT_RECORDS -> AssistantIntent.RECENT_RECORDS;
            case STATUS_BREAKDOWN -> AssistantIntent.STATUS_BREAKDOWN;
            case LEAD_PIPELINE -> AssistantIntent.LEAD_PIPELINE;
            case ASSISTANT_HELP -> AssistantIntent.ASSISTANT_CAPABILITIES;
            case CRM_SUMMARY, MULTI_TOOL -> AssistantIntent.CRM_SUMMARY;
        };
    }

    private static List<AssistantToolCall> defaultToolCalls(AssistantRetrievalType type, String query) {
        String normalizedQuery = query == null ? "" : query.trim();
        return switch (type) {
            case STALE_LEADS -> List.of(AssistantToolCall.of("crm.listStaleLeads"));
            case LEADS_WITHOUT_CONTACTS -> List.of(AssistantToolCall.of("crm.listLeadsWithoutContactPersons"));
            case MARKETING_SOURCE_ANALYTICS -> List.of(AssistantToolCall.of("crm.getMarketingSourcePerformance"));
            case BUSINESS_PARTNER_DETAILS -> List.of(AssistantToolCall.of("crm.getBusinessPartnerDetails", Map.of("query", normalizedQuery)));
            case CRM_SEARCH -> List.of(AssistantToolCall.of("crm.searchRecords", Map.of("query", normalizedQuery)));
            case CONTACT_COVERAGE -> List.of(AssistantToolCall.of("crm.getContactCoverage"));
            case RECENT_RECORDS -> List.of(AssistantToolCall.of("crm.getRecentRecords"));
            case STATUS_BREAKDOWN -> List.of(AssistantToolCall.of("crm.getStatusBreakdown"));
            case LEAD_PIPELINE -> List.of(AssistantToolCall.of("crm.getLeadPipeline"));
            case ASSISTANT_HELP -> List.of();
            case CRM_SUMMARY, MULTI_TOOL -> List.of(AssistantToolCall.of("crm.getCrmSummary"));
        };
    }
}

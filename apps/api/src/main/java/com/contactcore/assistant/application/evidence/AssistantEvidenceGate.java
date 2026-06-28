// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.evidence;

import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import com.contactcore.assistant.retrieval.AssistantSearchResult;
import com.contactcore.assistant.tool.AssistantToolResult;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AssistantEvidenceGate {
    private static final String BUSINESS_PARTNER = "BUSINESS_PARTNER";

    public AssistantEvidence assess(AssistantPlan plan, AssistantRetrievalResult retrieval) {
        if (requiresNoCrmEvidence(plan.intent())) {
            return new AssistantEvidence(retrieval, true, "No CRM evidence required for this safe deterministic assistant response.");
        }
        if (requiresBusinessPartnerEvidence(plan.intent())) {
            return new AssistantEvidence(
                    filterRecords(retrieval, this::isBusinessPartnerEvidence),
                    false,
                    "Record-level business-partner evidence required. Aggregate CRM records are not valid proof."
            );
        }
        if (requiresLeadRecordEvidence(plan.intent())) {
            return new AssistantEvidence(
                    filterRecords(retrieval, this::isLeadBusinessPartnerEvidence),
                    false,
                    "Lead record evidence required. Aggregate CRM records are not valid proof."
            );
        }
        return new AssistantEvidence(retrieval, true, "Aggregate and record-level evidence are valid for this intent.");
    }

    private boolean requiresNoCrmEvidence(AssistantIntent intent) {
        return intent == AssistantIntent.GREETING ||
                intent == AssistantIntent.ASSISTANT_IDENTITY ||
                intent == AssistantIntent.ASSISTANT_CAPABILITIES ||
                intent == AssistantIntent.UNCLEAR_REQUEST ||
                intent == AssistantIntent.UNSUPPORTED;
    }

    private boolean requiresBusinessPartnerEvidence(AssistantIntent intent) {
        return intent == AssistantIntent.BUSINESS_PARTNER_EXISTENCE ||
                intent == AssistantIntent.BUSINESS_PARTNER_SEARCH ||
                intent == AssistantIntent.BUSINESS_PARTNER_DETAILS;
    }

    private boolean requiresLeadRecordEvidence(AssistantIntent intent) {
        return intent == AssistantIntent.LEADS_MISSING_CONTACTS || intent == AssistantIntent.LEADS_NEED_FOLLOW_UP;
    }

    private AssistantRetrievalResult filterRecords(AssistantRetrievalResult retrieval,
                                                   java.util.function.Predicate<AssistantSearchResult> predicate) {
        List<AssistantToolResult> filteredTools = retrieval.toolResults().stream()
                .map(toolResult -> new AssistantToolResult(
                        toolResult.toolName(),
                        toolResult.summary(),
                        toolResult.records().stream().filter(predicate).toList()
                ))
                .toList();
        return new AssistantRetrievalResult(retrieval.type(), retrieval.userIntent(), filteredTools);
    }

    private boolean isBusinessPartnerEvidence(AssistantSearchResult result) {
        return BUSINESS_PARTNER.equals(result.reference().entityType());
    }

    private boolean isLeadBusinessPartnerEvidence(AssistantSearchResult result) {
        if (!isBusinessPartnerEvidence(result)) {
            return false;
        }
        String kind = result.fields().getOrDefault("Kind", "");
        return kind.isBlank() || "LEAD".equalsIgnoreCase(kind) || "Lead".equalsIgnoreCase(kind);
    }
}

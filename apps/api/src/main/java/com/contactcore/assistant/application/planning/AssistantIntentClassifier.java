// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.planning;

import com.contactcore.assistant.application.AssistantEntityExtractor;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AssistantIntentClassifier {
    private static final Set<String> GREETING_PHRASES = Set.of(
            "hi",
            "hello",
            "hey",
            "hey there",
            "hi there",
            "hello there",
            "good morning",
            "good afternoon",
            "good evening",
            "hallo",
            "moin"
    );

    public AssistantIntent classify(String normalizedMessage, AssistantEntityExtractor.AssistantCriteria criteria) {
        String lower = normalizeForClassification(normalizedMessage);

        if (isUnsupportedWriteRequest(lower)) {
            return AssistantIntent.UNSUPPORTED;
        }
        if (isGreeting(lower)) {
            return AssistantIntent.GREETING;
        }
        if (isIdentityQuestion(lower)) {
            return AssistantIntent.ASSISTANT_IDENTITY;
        }
        if (isCapabilitiesQuestion(lower)) {
            return AssistantIntent.ASSISTANT_CAPABILITIES;
        }
        if (isExistenceQuestion(lower, criteria)) {
            return AssistantIntent.BUSINESS_PARTNER_EXISTENCE;
        }
        if (isDetailsQuestion(lower, criteria)) {
            return AssistantIntent.BUSINESS_PARTNER_DETAILS;
        }
        if (isLeadMissingContactsQuestion(lower)) {
            return AssistantIntent.LEADS_MISSING_CONTACTS;
        }
        if (isLeadFollowUpQuestion(lower)) {
            return AssistantIntent.LEADS_NEED_FOLLOW_UP;
        }
        if (isMarketingPerformanceQuestion(lower)) {
            return AssistantIntent.MARKETING_PERFORMANCE;
        }
        if (isRecentRecordsQuestion(lower)) {
            return AssistantIntent.RECENT_RECORDS;
        }
        if (isLeadPipelineQuestion(lower)) {
            return AssistantIntent.LEAD_PIPELINE;
        }
        if (isStatusBreakdownQuestion(lower)) {
            return AssistantIntent.STATUS_BREAKDOWN;
        }
        if (isContactCoverageQuestion(lower)) {
            return AssistantIntent.CONTACT_COVERAGE;
        }
        if (isBusinessPartnerSearchQuestion(lower, criteria)) {
            return AssistantIntent.BUSINESS_PARTNER_SEARCH;
        }
        if (isSummaryQuestion(lower)) {
            return AssistantIntent.CRM_SUMMARY;
        }
        return AssistantIntent.UNCLEAR_REQUEST;
    }

    private String normalizeForClassification(String message) {
        return (message == null ? "" : message)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\p{Punct}&&[^/_-]]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean isUnsupportedWriteRequest(String lower) {
        return lower.matches(".*\\b(delete|remove|archive|update|edit|create)\\b.*")
                || containsAny(lower, "send email", "send mail", "call customer");
    }

    private boolean isGreeting(String lower) {
        return GREETING_PHRASES.contains(lower);
    }

    private boolean isIdentityQuestion(String lower) {
        return lower.equals("what are you")
                || lower.equals("who are you")
                || lower.equals("what is this assistant")
                || lower.equals("who is contactcore assistant")
                || lower.equals("what is contactcore assistant")
                || containsAny(lower, "tell me what you are", "explain what you are");
    }

    private boolean isCapabilitiesQuestion(String lower) {
        return lower.equals("help")
                || lower.equals("assistant help")
                || containsAny(
                        lower,
                        "what can you help",
                        "how can you help",
                        "what do you do",
                        "what can i ask",
                        "what questions can i ask",
                        "what are your capabilities",
                        "what can you do",
                        "what can this assistant do",
                        "how do i use this assistant"
                );
    }

    private boolean isExistenceQuestion(String lower, AssistantEntityExtractor.AssistantCriteria criteria) {
        if (criteria.searchTerm().isBlank() || isAggregateCountQuestion(lower)) {
            return false;
        }
        return containsAny(lower, "do we have", "is there", "are there", "does ", "exist", "exists") &&
                containsAny(lower, "customer", "supplier", "lead", "business partner", "record", "name", "named", "called");
    }

    private boolean isAggregateCountQuestion(String lower) {
        return containsAny(lower, "how many", "count", "number of") && hasCrmEntityAnchor(lower);
    }

    private boolean isDetailsQuestion(String lower, AssistantEntityExtractor.AssistantCriteria criteria) {
        return !criteria.searchTerm().isBlank() && containsAny(lower, "details", "detail", "summarize customer", "summarize supplier", "summarize lead", "profile of");
    }

    private boolean isLeadMissingContactsQuestion(String lower) {
        return containsAny(lower, "missing contact", "without contact", "no contact", "contact person") && containsAny(lower, "lead", "leads");
    }

    private boolean isLeadFollowUpQuestion(String lower) {
        return containsAny(lower, "follow-up", "follow up", "stale", "old lead", "need attention", "older than");
    }

    private boolean isMarketingPerformanceQuestion(String lower) {
        return containsAny(lower, "marketing source", "lead source", "source performance", "campaign");
    }

    private boolean isRecentRecordsQuestion(String lower) {
        return containsAny(lower, "recent", "created recently", "new records", "latest");
    }

    private boolean isLeadPipelineQuestion(String lower) {
        return containsAny(lower, "pipeline", "lead pipeline", "lead status");
    }

    private boolean isStatusBreakdownQuestion(String lower) {
        return containsAny(lower, "status breakdown", "status distribution", "by status");
    }

    private boolean isContactCoverageQuestion(String lower) {
        return containsAny(lower, "contact coverage") ||
                (containsAny(lower, "contact", "contacts") &&
                        containsAny(lower, "coverage", "report", "summary", "status"));
    }

    private boolean isBusinessPartnerSearchQuestion(String lower, AssistantEntityExtractor.AssistantCriteria criteria) {
        if (criteria.searchTerm().isBlank()) {
            return false;
        }
        return containsAny(lower, "find", "search", "show me", "related to") || hasCrmEntityAnchor(lower);
    }

    private boolean isSummaryQuestion(String lower) {
        return containsAny(lower, "summary", "summarize", "overview", "report")
                || isAggregateCountQuestion(lower)
                || (containsAny(lower, "status") && hasCrmEntityAnchor(lower));
    }

    private boolean hasCrmEntityAnchor(String lower) {
        return containsAny(
                lower,
                "crm",
                "customer",
                "customers",
                "supplier",
                "suppliers",
                "lead",
                "leads",
                "business partner",
                "business partners",
                "record",
                "records"
        );
    }

    private boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}

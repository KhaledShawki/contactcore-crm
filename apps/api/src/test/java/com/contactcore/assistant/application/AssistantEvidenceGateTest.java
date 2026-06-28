// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.application.evidence.AssistantEvidenceGate;
import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.retrieval.AssistantSearchResult;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.assistant.tool.AssistantToolResult;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AssistantEvidenceGateTest {
    private final AssistantEvidenceGate evidenceGate = new AssistantEvidenceGate();


    @Test
    void doesNotRequireCrmEvidenceForAssistantIdentity() {
        AssistantPlan plan = new AssistantPlan(
                AssistantRetrievalType.ASSISTANT_HELP,
                AssistantIntent.ASSISTANT_IDENTITY,
                "what are you?",
                "",
                "",
                "",
                20,
                List.of(),
                AssistantIntent.ASSISTANT_IDENTITY.name()
        );
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(AssistantRetrievalType.ASSISTANT_HELP, List.of());

        assertThat(evidenceGate.assess(plan, retrieval).retrieval().results()).isEmpty();
        assertThat(evidenceGate.assess(plan, retrieval).valid()).isTrue();
    }

    @Test
    void rejectsAggregateEvidenceForBusinessPartnerExistence() {
        AssistantPlan plan = new AssistantPlan(
                AssistantRetrievalType.CRM_SEARCH,
                AssistantIntent.BUSINESS_PARTNER_EXISTENCE,
                "Do we have customer with name X?",
                "X",
                "CUSTOMER",
                "",
                20,
                List.of(AssistantToolCall.of("crm.searchRecords", Map.of("query", "X", "kindCode", "CUSTOMER"))),
                "BUSINESS_PARTNER_EXISTENCE for X [CUSTOMER]"
        );
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(
                AssistantRetrievalType.CRM_SEARCH,
                "test",
                List.of(new AssistantToolResult("crm.getCrmSummary", "summary", List.of(
                        AssistantSearchResult.of(new AssistantRecordReference("CRM_SUMMARY", 1L, "Customer", "/customers"), fields("Kind", "Customer")),
                        AssistantSearchResult.of(new AssistantRecordReference("BUSINESS_PARTNER", 7L, "CUS-007 - Customer X", "/customers/7"), fields("Kind", "CUSTOMER"))
                )))
        );

        AssistantRetrievalResult filtered = evidenceGate.assess(plan, retrieval).retrieval();

        assertThat(filtered.results()).hasSize(1);
        assertThat(filtered.references().getFirst().entityType()).isEqualTo("BUSINESS_PARTNER");
    }

    @Test
    void keepsAggregateEvidenceForCrmSummary() {
        AssistantPlan plan = new AssistantPlan(AssistantRetrievalType.CRM_SUMMARY, "summary", 20);
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(
                AssistantRetrievalType.CRM_SUMMARY,
                List.of(AssistantSearchResult.of(new AssistantRecordReference("CRM_SUMMARY", 1L, "Customer", "/customers"), fields("Kind", "Customer")))
        );

        assertThat(evidenceGate.assess(plan, retrieval).retrieval().results()).hasSize(1);
    }

    private LinkedHashMap<String, String> fields(String firstKey, String firstValue, String... rest) {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put(firstKey, firstValue);
        for (int index = 0; index < rest.length; index += 2) {
            values.put(rest[index], rest[index + 1]);
        }
        return values;
    }
}

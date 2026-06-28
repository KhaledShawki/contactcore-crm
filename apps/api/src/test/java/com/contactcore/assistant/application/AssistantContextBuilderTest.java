// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.retrieval.AssistantSearchResult;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

class AssistantContextBuilderTest {
    @Test
    void buildsTraceableContextWithReferences() {
        AssistantContextBuilder builder = new AssistantContextBuilder(properties(12000));
        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("Code", "LED-001");
        fields.put("Name", "Meyer Digital GmbH");
        AssistantRecordReference reference = new AssistantRecordReference("BUSINESS_PARTNER", 7L, "LED-001 - Meyer Digital GmbH", "/leads/7");

        AssistantContext context = builder.build(new AssistantRetrievalResult(
                AssistantRetrievalType.CRM_SEARCH,
                List.of(AssistantSearchResult.of(reference, fields))
        ));

        assertThat(context.systemPrompt()).contains("Answer only from TOOL_RESULTS");
        assertThat(context.contextText()).contains("LED-001", "/leads/7");
        assertThat(context.references()).containsExactly(reference);
    }

    @Test
    void respectsContextSizeLimit() {
        AssistantContextBuilder builder = new AssistantContextBuilder(properties(1200));
        LinkedHashMap<String, String> fields = new LinkedHashMap<>();
        fields.put("Long", "x".repeat(3000));

        AssistantContext context = builder.build(new AssistantRetrievalResult(
                AssistantRetrievalType.CRM_SUMMARY,
                List.of(AssistantSearchResult.of(new AssistantRecordReference("CRM_SUMMARY", 0L, "Summary", "/reports"), fields))
        ));

        assertThat(context.contextText()).hasSizeLessThanOrEqualTo(1200);
    }

    private AssistantProperties properties(int maxContextChars) {
        return new AssistantProperties(true, "noop", "test", "", "", 5000, maxContextChars, 20, 10);
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.contactcore.assistant.application.answer.AssistantAnswerGenerationResult;
import com.contactcore.assistant.application.answer.AssistantAnswerGenerationService;
import com.contactcore.assistant.application.answer.AssistantAnswerSource;
import com.contactcore.assistant.application.answer.AssistantAnswerStatus;
import com.contactcore.assistant.application.answer.DegradedAssistantAnswerGenerator;
import com.contactcore.assistant.application.answer.DeterministicCrmAnswerGenerator;
import com.contactcore.assistant.application.answer.AssistantStaticResponseCatalog;
import com.contactcore.assistant.application.answer.LlmAssistantAnswerGenerator;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.retrieval.AssistantSearchResult;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssistantAnswerGenerationServiceTest {
    @Mock
    private LlmAssistantAnswerGenerator llmAnswerGenerator;

    @Test
    void usesDeterministicAnswerForExactCrmListQuestion() {
        DeterministicCrmAnswerGenerator deterministic = new DeterministicCrmAnswerGenerator(new AssistantStaticResponseCatalog());
        AssistantAnswerGenerationService service = new AssistantAnswerGenerationService(
                deterministic,
                llmAnswerGenerator,
                new DegradedAssistantAnswerGenerator(deterministic)
        );
        AssistantPlan plan = new AssistantPlan(AssistantRetrievalType.LEADS_WITHOUT_CONTACTS, "", 20);
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(
                AssistantRetrievalType.LEADS_WITHOUT_CONTACTS,
                List.of(AssistantSearchResult.of(
                        new AssistantRecordReference("BUSINESS_PARTNER", 7L, "LED-007 - Meyer Digital GmbH", "/leads/7"),
                        fields("Status", "New", "Marketing source", "Website", "Contact persons", "0")
                ))
        );

        AssistantAnswerGenerationResult result = service.generate(plan, retrieval, "list leads without contact persons");

        assertThat(result.status()).isEqualTo(AssistantAnswerStatus.SUCCESS);
        assertThat(result.source()).isEqualTo(AssistantAnswerSource.DETERMINISTIC);
        assertThat(result.modelName()).isEqualTo(DeterministicCrmAnswerGenerator.MODEL_NAME);
        assertThat(result.answer()).contains("without contact persons", "Meyer Digital GmbH", "contact persons: 0");
        verifyNoInteractions(llmAnswerGenerator);
    }

    @Test
    void returnsDegradedAnswerWhenLlmFailsAfterSuccessfulRetrieval() {
        DeterministicCrmAnswerGenerator deterministic = new DeterministicCrmAnswerGenerator(new AssistantStaticResponseCatalog());
        DegradedAssistantAnswerGenerator degraded = new DegradedAssistantAnswerGenerator(deterministic);
        AssistantAnswerGenerationService service = new AssistantAnswerGenerationService(deterministic, llmAnswerGenerator, degraded);
        AssistantPlan plan = new AssistantPlan(AssistantRetrievalType.CRM_SUMMARY, "", 20);
        AssistantRetrievalResult retrieval = new AssistantRetrievalResult(
                AssistantRetrievalType.CRM_SUMMARY,
                List.of(AssistantSearchResult.of(
                        new AssistantRecordReference("CRM_SUMMARY", 1L, "Lead", "/leads"),
                        fields("Kind", "Lead", "Active records", "12")
                ))
        );
        when(llmAnswerGenerator.generate(plan, retrieval, "summarize CRM"))
                .thenThrow(new IllegalStateException("llama.cpp is unavailable"));

        AssistantAnswerGenerationResult result = service.generate(plan, retrieval, "summarize CRM");

        assertThat(result.status()).isEqualTo(AssistantAnswerStatus.DEGRADED);
        assertThat(result.source()).isEqualTo(AssistantAnswerSource.FALLBACK);
        assertThat(result.warning()).contains("language model was unavailable");
        assertThat(result.failureReason()).contains("llama.cpp is unavailable");
        assertThat(result.answer()).contains("Lead");
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

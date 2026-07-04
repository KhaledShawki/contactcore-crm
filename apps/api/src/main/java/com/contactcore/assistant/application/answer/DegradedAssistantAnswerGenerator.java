// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.answer;

import com.contactcore.assistant.application.AssistantLocaleContext;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DegradedAssistantAnswerGenerator {
    private final DeterministicCrmAnswerGenerator deterministicAnswerGenerator;
    private final AssistantStaticResponseCatalog staticResponses;

    public DegradedAssistantAnswerGenerator(DeterministicCrmAnswerGenerator deterministicAnswerGenerator) {
        this(deterministicAnswerGenerator, new AssistantStaticResponseCatalog());
    }

    @Autowired
    public DegradedAssistantAnswerGenerator(DeterministicCrmAnswerGenerator deterministicAnswerGenerator, AssistantStaticResponseCatalog staticResponses) {
        this.deterministicAnswerGenerator = deterministicAnswerGenerator;
        this.staticResponses = staticResponses;
    }

    public AssistantAnswerGenerationResult generate(AssistantPlan plan, AssistantRetrievalResult retrieval, RuntimeException failure, AssistantLocaleContext locale) {
        String answer = deterministicAnswerGenerator.buildAnswer(plan, retrieval, locale);
        String modelName = plan == null ? "assistant-fallback" : "assistant-fallback-" + plan.retrievalType().name().toLowerCase();
        return AssistantAnswerGenerationResult.degraded(answer, modelName, staticResponses.message(locale, "assistant.warning.degraded", "The language model was unavailable. Showing a CRM-generated answer from backend tool results."), sanitize(failure.getMessage()));
    }

    private String sanitize(String message) {
        if (message == null || message.isBlank()) {
            return "Language model provider failed without a message.";
        }
        return message.length() <= 500 ? message : message.substring(0, 500);
    }
}

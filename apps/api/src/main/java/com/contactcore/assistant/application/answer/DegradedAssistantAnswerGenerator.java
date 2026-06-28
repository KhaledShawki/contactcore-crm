// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.answer;

import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import org.springframework.stereotype.Component;

@Component
public class DegradedAssistantAnswerGenerator {
    private static final String WARNING = "The language model was unavailable. Showing a CRM-generated answer from backend tool results.";
    private final DeterministicCrmAnswerGenerator deterministicAnswerGenerator;

    public DegradedAssistantAnswerGenerator(DeterministicCrmAnswerGenerator deterministicAnswerGenerator) {
        this.deterministicAnswerGenerator = deterministicAnswerGenerator;
    }

    public AssistantAnswerGenerationResult generate(AssistantPlan plan, AssistantRetrievalResult retrieval, RuntimeException failure) {
        String answer = deterministicAnswerGenerator.buildAnswer(plan, retrieval);
        String modelName = plan == null ? "assistant-fallback" : "assistant-fallback-" + plan.retrievalType().name().toLowerCase();
        return AssistantAnswerGenerationResult.degraded(answer, modelName, WARNING, sanitize(failure.getMessage()));
    }

    private String sanitize(String message) {
        if (message == null || message.isBlank()) {
            return "Language model provider failed without a message.";
        }
        return message.length() <= 500 ? message : message.substring(0, 500);
    }
}

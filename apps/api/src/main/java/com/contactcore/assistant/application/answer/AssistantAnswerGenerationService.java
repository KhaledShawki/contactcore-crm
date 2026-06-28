// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.answer;

import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import org.springframework.stereotype.Service;

@Service
public class AssistantAnswerGenerationService {
    private final DeterministicCrmAnswerGenerator deterministicAnswerGenerator;
    private final LlmAssistantAnswerGenerator llmAnswerGenerator;
    private final DegradedAssistantAnswerGenerator degradedAnswerGenerator;

    public AssistantAnswerGenerationService(DeterministicCrmAnswerGenerator deterministicAnswerGenerator,
                                            LlmAssistantAnswerGenerator llmAnswerGenerator,
                                            DegradedAssistantAnswerGenerator degradedAnswerGenerator) {
        this.deterministicAnswerGenerator = deterministicAnswerGenerator;
        this.llmAnswerGenerator = llmAnswerGenerator;
        this.degradedAnswerGenerator = degradedAnswerGenerator;
    }

    public AssistantAnswerGenerationResult generate(AssistantPlan plan, AssistantRetrievalResult retrieval, String userMessage) {
        if (deterministicAnswerGenerator.supports(plan, retrieval)) {
            return deterministicAnswerGenerator.generate(plan, retrieval, userMessage);
        }

        try {
            return llmAnswerGenerator.generate(plan, retrieval, userMessage);
        } catch (RuntimeException providerFailure) {
            return degradedAnswerGenerator.generate(plan, retrieval, providerFailure);
        }
    }
}

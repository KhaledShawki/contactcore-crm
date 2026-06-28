// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.answer;

import com.contactcore.assistant.application.AssistantContext;
import com.contactcore.assistant.application.AssistantContextBuilder;
import com.contactcore.assistant.application.AssistantProperties;
import com.contactcore.assistant.llm.LlmGateway;
import com.contactcore.assistant.llm.LlmRequest;
import com.contactcore.assistant.llm.LlmResponse;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import org.springframework.stereotype.Component;

@Component
public class LlmAssistantAnswerGenerator implements AssistantAnswerGenerator {
    private final AssistantProperties properties;
    private final AssistantContextBuilder contextBuilder;
    private final LlmGateway llmGateway;

    public LlmAssistantAnswerGenerator(AssistantProperties properties,
                                       AssistantContextBuilder contextBuilder,
                                       LlmGateway llmGateway) {
        this.properties = properties;
        this.contextBuilder = contextBuilder;
        this.llmGateway = llmGateway;
    }

    @Override
    public boolean supports(AssistantPlan plan, AssistantRetrievalResult retrieval) {
        return true;
    }

    @Override
    public AssistantAnswerGenerationResult generate(AssistantPlan plan, AssistantRetrievalResult retrieval, String userMessage) {
        AssistantContext context = contextBuilder.build(retrieval);
        LlmResponse response = llmGateway.complete(new LlmRequest(
                properties.model(),
                plan.retrievalType(),
                context.systemPrompt(),
                context.contextText(),
                userMessage
        ));
        return AssistantAnswerGenerationResult.success(AssistantAnswerSource.LLM, response.content(), response.modelName());
    }
}

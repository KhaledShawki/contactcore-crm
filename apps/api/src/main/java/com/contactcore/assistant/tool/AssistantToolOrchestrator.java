// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool;

import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssistantToolOrchestrator {
    private final AssistantToolRegistry registry;

    public AssistantToolOrchestrator(AssistantToolRegistry registry) {
        this.registry = registry;
    }

    @Transactional(readOnly = true)
    public AssistantRetrievalResult execute(Long userId, AssistantPlan plan) {
        AssistantToolExecutionContext context = new AssistantToolExecutionContext(userId, plan.maxResults());
        return new AssistantRetrievalResult(
                plan.retrievalType(),
                plan.userIntent(),
                plan.toolCalls().stream()
                        .map(call -> registry.require(call.toolName()).execute(call, context))
                        .toList()
        );
    }
}

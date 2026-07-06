// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool;

import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import com.contactcore.assistant.security.AssistantToolAuthorizationGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssistantToolOrchestrator {
    private final AssistantToolRegistry registry;
    private final AssistantToolAuthorizationGuard authorization;

    public AssistantToolOrchestrator(AssistantToolRegistry registry, AssistantToolAuthorizationGuard authorization) {
        this.registry = registry;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public AssistantRetrievalResult execute(Long userId, AssistantPlan plan) {
        AssistantToolExecutionContext context = new AssistantToolExecutionContext(userId, plan.maxResults(), plan.retrievalType());
        return new AssistantRetrievalResult(
                plan.retrievalType(),
                plan.userIntent(),
                plan.toolCalls().stream()
                        .map(call -> execute(call, context))
                        .toList()
        );
    }

    private AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context) {
        authorization.requireToolExecution(call, context.retrievalType());
        return registry.require(call.toolName()).execute(call, context);
    }
}

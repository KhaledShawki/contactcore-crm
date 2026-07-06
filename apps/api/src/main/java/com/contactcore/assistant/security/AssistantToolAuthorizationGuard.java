// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import org.springframework.stereotype.Component;

@Component
public class AssistantToolAuthorizationGuard {
    private final CurrentIamAuthorizationService authorization;
    private final ContactCoreTenantContext tenantContext;
    private final AssistantToolAuthorizationRegistry rules;

    public AssistantToolAuthorizationGuard(CurrentIamAuthorizationService authorization,
                                           ContactCoreTenantContext tenantContext,
                                           AssistantToolAuthorizationRegistry rules) {
        this.authorization = authorization;
        this.tenantContext = tenantContext;
        this.rules = rules;
    }

    public void requireToolExecution(AssistantToolCall call, AssistantRetrievalType retrievalType) {
        AssistantToolAuthorizationRule rule = rules.require(call.toolName());
        require(rule, call, retrievalType);
    }

    public void requireConnectorBusinessPartnerSearch(AssistantToolCall call, AssistantRetrievalType retrievalType) {
        AssistantToolAuthorizationRule rule = rules.connectorBusinessPartnerSearchRule(call.toolName());
        require(rule, call, retrievalType);
    }

    private void require(AssistantToolAuthorizationRule rule, AssistantToolCall call, AssistantRetrievalType retrievalType) {
        String tenantId = tenantContext.currentTenantId();
        var context = AssistantAuthorizationContext.forTool(call, rule.category(), retrievalType).toRequestContext();
        authorization.requireAllowed(rule.category().action(), rule.assistantResource(tenantId), context);
        authorization.requireAllowed(rule.moduleAction(), rule.moduleResource(tenantId), context);
    }
}

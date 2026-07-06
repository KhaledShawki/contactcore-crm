// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.assistant.api.AssistantRequest;
import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.shared.api.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class AssistantAuthorizationGuard {
    private final CurrentIamAuthorizationService authorization;
    private final ContactCoreTenantContext tenantContext;

    public AssistantAuthorizationGuard(CurrentIamAuthorizationService authorization, ContactCoreTenantContext tenantContext) {
        this.authorization = authorization;
        this.tenantContext = tenantContext;
    }

    public void requireAsk(AssistantRequest request) {
        authorization.requireAllowed(
                AssistantIamActions.ASK,
                AssistantIamResources.sessions(tenantId()),
                AssistantAuthorizationContext.forAsk(request == null ? null : request.conversationId(), request == null ? null : request.message())
                        .toRequestContext()
        );
    }

    public void requireReadConversations() {
        authorization.requireAllowed(
                AssistantIamActions.READ_CONVERSATIONS,
                AssistantIamResources.conversations(tenantId()),
                AssistantAuthorizationContext.forConversation(null, "listConversations").toRequestContext()
        );
    }

    public void requireReadConversation(Long conversationId) {
        authorization.requireAllowed(
                AssistantIamActions.READ_CONVERSATIONS,
                AssistantIamResources.conversation(tenantId(), requireId(conversationId, "conversationId")),
                AssistantAuthorizationContext.forConversation(conversationId, "readConversation").toRequestContext()
        );
    }

    public void requireArchiveConversation(Long conversationId) {
        authorization.requireAllowed(
                AssistantIamActions.ARCHIVE_CONVERSATION,
                AssistantIamResources.conversation(tenantId(), requireId(conversationId, "conversationId")),
                AssistantAuthorizationContext.forConversation(conversationId, "archiveConversation").toRequestContext()
        );
    }

    private String tenantId() {
        return tenantContext.currentTenantId();
    }

    private static Long requireId(Long value, String fieldName) {
        if (value == null) {
            throw new InvalidRequestException(fieldName + " must not be null");
        }
        return value;
    }
}

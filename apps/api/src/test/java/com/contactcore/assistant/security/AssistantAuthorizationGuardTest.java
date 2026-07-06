// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.assistant.api.AssistantRequest;
import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.iam.domain.IamResource;
import com.contactcore.iam.evaluation.IamRequestContext;
import com.contactcore.shared.api.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AssistantAuthorizationGuardTest {
    private final CurrentIamAuthorizationService authorization = mock(CurrentIamAuthorizationService.class);
    private final ContactCoreTenantContext tenantContext = mock(ContactCoreTenantContext.class);
    private final AssistantAuthorizationGuard guard = new AssistantAuthorizationGuard(authorization, tenantContext);

    @BeforeEach
    void setUp() {
        when(tenantContext.currentTenantId()).thenReturn("tenant-1");
    }

    @Test
    void requiresAskWithSessionResourceAndRequestContext() {
        guard.requireAsk(new AssistantRequest(77L, "Show open leads"));

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(AssistantIamActions.ASK),
                eq(IamResource.of("contactcore:tenant-1:assistant:session/*")),
                context.capture()
        );
        assertThat(context.getValue().get("conversationId")).isEqualTo(77L);
        assertThat(context.getValue().get("query")).isEqualTo("Show open leads");
        assertThat(context.getValue().get("operation")).isEqualTo("ask");
    }

    @Test
    void requiresReadConversationsWithConversationCollectionResource() {
        guard.requireReadConversations();

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(AssistantIamActions.READ_CONVERSATIONS),
                eq(IamResource.of("contactcore:tenant-1:assistant:conversation/*")),
                context.capture()
        );
        assertThat(context.getValue().get("operation")).isEqualTo("listConversations");
    }

    @Test
    void requiresReadConversationWithSpecificConversationResource() {
        guard.requireReadConversation(42L);

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(AssistantIamActions.READ_CONVERSATIONS),
                eq(IamResource.of("contactcore:tenant-1:assistant:conversation/42")),
                context.capture()
        );
        assertThat(context.getValue().get("conversationId")).isEqualTo(42L);
        assertThat(context.getValue().get("operation")).isEqualTo("readConversation");
    }

    @Test
    void requiresArchiveConversationWithSpecificConversationResource() {
        guard.requireArchiveConversation(42L);

        ArgumentCaptor<IamRequestContext> context = ArgumentCaptor.forClass(IamRequestContext.class);
        verify(authorization).requireAllowed(
                eq(AssistantIamActions.ARCHIVE_CONVERSATION),
                eq(IamResource.of("contactcore:tenant-1:assistant:conversation/42")),
                context.capture()
        );
        assertThat(context.getValue().get("conversationId")).isEqualTo(42L);
        assertThat(context.getValue().get("operation")).isEqualTo("archiveConversation");
    }

    @Test
    void rejectsMissingConversationIdForSpecificConversationOperations() {
        assertThatThrownBy(() -> guard.requireReadConversation(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("conversationId");
        assertThatThrownBy(() -> guard.requireArchiveConversation(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("conversationId");
    }
}

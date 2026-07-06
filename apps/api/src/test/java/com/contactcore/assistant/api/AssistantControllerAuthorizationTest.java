// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.contactcore.assistant.application.AssistantApplicationService;
import com.contactcore.assistant.security.AssistantAuthorizationGuard;
import com.contactcore.iam.application.IamAccessDeniedException;
import com.contactcore.security.application.UserPrincipal;
import com.contactcore.security.domain.AppUser;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

class AssistantControllerAuthorizationTest {
    private final AssistantApplicationService service = mock(AssistantApplicationService.class);
    private final AssistantAuthorizationGuard authorization = mock(AssistantAuthorizationGuard.class);
    private final AssistantController controller = new AssistantController(service, authorization);

    @Test
    void authorizesBeforeSendingMessage() {
        UserPrincipal principal = principal();
        AssistantRequest request = new AssistantRequest(null, "Show CRM summary");
        when(service.sendMessage(42L, request)).thenReturn(response());

        controller.sendMessage(principal, request);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireAsk(request);
        inOrder.verify(service).sendMessage(42L, request);
    }

    @Test
    void skipsSendingMessageWhenAuthorizationFails() {
        AssistantRequest request = new AssistantRequest(null, "Show CRM summary");
        IamAccessDeniedException denied = mock(IamAccessDeniedException.class);
        org.mockito.Mockito.doThrow(denied).when(authorization).requireAsk(request);

        assertThatThrownBy(() -> controller.sendMessage(principal(), request))
                .isSameAs(denied);

        verify(service, never()).sendMessage(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void authorizesBeforeListingConversations() {
        UserPrincipal principal = principal();
        when(service.conversations(42L)).thenReturn(List.of());

        controller.conversations(principal);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireReadConversations();
        inOrder.verify(service).conversations(42L);
    }

    @Test
    void authorizesBeforeReadingConversation() {
        UserPrincipal principal = principal();
        when(service.conversation(42L, 7L)).thenReturn(new AssistantConversationDetailResponse(7L, "Title", Instant.now(), Instant.now(), List.of()));

        controller.conversation(principal, 7L);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireReadConversation(7L);
        inOrder.verify(service).conversation(42L, 7L);
    }

    @Test
    void authorizesBeforeArchivingConversation() {
        UserPrincipal principal = principal();

        controller.archiveConversation(principal, 7L);

        InOrder inOrder = inOrder(authorization, service);
        inOrder.verify(authorization).requireArchiveConversation(7L);
        inOrder.verify(service).archiveConversation(42L, 7L);
    }

    private static AssistantResponse response() {
        return new AssistantResponse(1L, "answer", "CRM_SUMMARY", "test", "OK", "STATIC", null, List.of(), Instant.now());
    }

    private static UserPrincipal principal() {
        AppUser user = new AppUser("khaled", "khaled@example.com", "hash");
        ReflectionTestUtils.setField(user, "id", 42L);
        return UserPrincipal.from(user);
    }
}

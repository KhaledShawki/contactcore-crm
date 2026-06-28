// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import com.contactcore.assistant.domain.AssistantConversation;
import com.contactcore.assistant.domain.AssistantConversationRepository;
import com.contactcore.assistant.domain.AssistantMessage;
import com.contactcore.assistant.domain.AssistantMessageReference;
import com.contactcore.assistant.domain.AssistantMessageRepository;
import com.contactcore.assistant.domain.AssistantMessageRole;
import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.shared.api.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssistantPersistenceService {
    private final AssistantConversationRepository conversations;
    private final AssistantMessageRepository messages;

    public AssistantPersistenceService(AssistantConversationRepository conversations, AssistantMessageRepository messages) {
        this.conversations = conversations;
        this.messages = messages;
    }

    @Transactional
    public AssistantUserMessagePersistResult saveUserMessage(Long userId, Long conversationId, String userMessage, String title) {
        AssistantConversation conversation = conversationId == null
                ? conversations.save(new AssistantConversation(userId, title))
                : findConversation(userId, conversationId);
        AssistantMessage message = messages.save(new AssistantMessage(conversation, AssistantMessageRole.USER, userMessage));
        return new AssistantUserMessagePersistResult(conversation, message);
    }

    @Transactional
    public AssistantMessage saveAssistantMessage(Long userId, Long conversationId, String content, List<AssistantRecordReference> references) {
        AssistantConversation conversation = findConversation(userId, conversationId);
        AssistantMessage assistantMessage = new AssistantMessage(conversation, AssistantMessageRole.ASSISTANT, content);
        for (AssistantRecordReference reference : references) {
            if (reference.entityId() != null && reference.entityId() > 0) {
                assistantMessage.addReference(new AssistantMessageReference(
                        reference.entityType(),
                        reference.entityId(),
                        reference.label(),
                        reference.route()
                ));
            }
        }
        return messages.save(assistantMessage);
    }

    @Transactional(readOnly = true)
    public AssistantConversation findConversation(Long userId, Long id) {
        return conversations.findActiveByUserIdAndId(userId, id)
                .orElseThrow(() -> new NotFoundException("Assistant conversation not found."));
    }

    public record AssistantUserMessagePersistResult(
            AssistantConversation conversation,
            AssistantMessage message
    ) {}
}

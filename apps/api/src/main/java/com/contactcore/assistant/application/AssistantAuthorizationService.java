// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import com.contactcore.assistant.domain.AssistantConversation;
import com.contactcore.shared.api.InvalidRequestException;
import org.springframework.stereotype.Service;

@Service
public class AssistantAuthorizationService {
    public void verifyConversationOwner(Long userId, AssistantConversation conversation) {
        if (!conversation.getUserId().equals(userId)) {
            throw new InvalidRequestException("Assistant conversation does not belong to the current user.");
        }
    }
}

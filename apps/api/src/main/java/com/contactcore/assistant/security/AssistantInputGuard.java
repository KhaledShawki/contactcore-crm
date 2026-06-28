// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.shared.api.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class AssistantInputGuard {
    private static final int MAX_MESSAGE_LENGTH = 2000;

    private final PromptInjectionGuard promptInjectionGuard;

    public AssistantInputGuard(PromptInjectionGuard promptInjectionGuard) {
        this.promptInjectionGuard = promptInjectionGuard;
    }

    public String normalizeAndValidate(String message) {
        String normalized = message == null ? "" : message.trim().replaceAll("\\s+", " ");
        if (normalized.isBlank()) {
            throw new InvalidRequestException("Assistant message is required.");
        }
        if (normalized.length() > MAX_MESSAGE_LENGTH) {
            throw new InvalidRequestException("Assistant message must be at most 2000 characters.");
        }
        if (promptInjectionGuard.suspicious(normalized)) {
            throw new InvalidRequestException("Assistant request was rejected because it contains unsafe instruction text.");
        }
        return normalized;
    }
}

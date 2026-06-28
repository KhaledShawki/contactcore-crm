// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AssistantRequest(
        Long conversationId,
        @NotBlank(message = "Message is required.")
        @Size(max = 2000, message = "Message must be at most 2000 characters.")
        String message
) {}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.api;

import java.time.Instant;

public record AssistantConversationResponse(
        Long id,
        String title,
        Instant createdAt,
        Instant updatedAt
) {}

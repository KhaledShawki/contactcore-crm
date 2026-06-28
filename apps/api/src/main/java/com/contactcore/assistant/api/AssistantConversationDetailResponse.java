// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.api;

import java.time.Instant;
import java.util.List;

public record AssistantConversationDetailResponse(
        Long id,
        String title,
        Instant createdAt,
        Instant updatedAt,
        List<AssistantMessageResponse> messages
) {}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.api;

import java.time.Instant;
import java.util.List;

public record AssistantResponse(
        Long conversationId,
        String answer,
        String retrievalType,
        String modelName,
        String answerStatus,
        String answerSource,
        String warning,
        List<AssistantReferenceResponse> references,
        Instant createdAt
) {}

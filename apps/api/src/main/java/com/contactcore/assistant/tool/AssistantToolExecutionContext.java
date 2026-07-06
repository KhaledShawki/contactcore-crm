// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool;

import com.contactcore.assistant.retrieval.AssistantRetrievalType;

public record AssistantToolExecutionContext(
        Long userId,
        int maxResults,
        AssistantRetrievalType retrievalType
) {}

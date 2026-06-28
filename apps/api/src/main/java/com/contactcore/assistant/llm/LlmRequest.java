// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.llm;

import com.contactcore.assistant.retrieval.AssistantRetrievalType;

public record LlmRequest(
        String model,
        AssistantRetrievalType retrievalType,
        String systemPrompt,
        String contextText,
        String userMessage
) {}

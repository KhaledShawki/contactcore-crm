// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import com.contactcore.assistant.retrieval.AssistantRecordReference;
import java.util.List;

public record AssistantContext(
        String systemPrompt,
        String contextText,
        List<AssistantRecordReference> references
) {}

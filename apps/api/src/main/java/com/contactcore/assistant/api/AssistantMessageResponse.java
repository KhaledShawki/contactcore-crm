// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.api;

import java.time.Instant;
import java.util.List;

public record AssistantMessageResponse(
        Long id,
        String role,
        String content,
        List<AssistantReferenceResponse> references,
        Instant createdAt
) {}

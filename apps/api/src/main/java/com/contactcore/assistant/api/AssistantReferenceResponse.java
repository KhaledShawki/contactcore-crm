// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.api;

public record AssistantReferenceResponse(
        String entityType,
        Long entityId,
        String label,
        String route
) {}

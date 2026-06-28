// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.retrieval;

public record AssistantRecordReference(
        String entityType,
        Long entityId,
        String label,
        String route
) {}

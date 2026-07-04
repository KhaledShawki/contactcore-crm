// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

public record AssistantNluRequest(String message) {
    public AssistantNluRequest {
        message = message == null ? "" : message.trim();
    }
}

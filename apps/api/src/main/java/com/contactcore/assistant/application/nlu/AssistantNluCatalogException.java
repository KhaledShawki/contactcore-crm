// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

public class AssistantNluCatalogException extends IllegalStateException {
    public AssistantNluCatalogException(String message) {
        super(message);
    }

    public AssistantNluCatalogException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

public interface AssistantNluRouter {
    AssistantNluDecision route(AssistantNluRequest request);
}

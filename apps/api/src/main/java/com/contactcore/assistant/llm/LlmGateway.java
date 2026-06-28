// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.llm;

public interface LlmGateway {
    LlmResponse complete(LlmRequest request);
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool;

public interface AssistantTool {
    String name();

    String description();

    AssistantToolResult execute(AssistantToolCall call, AssistantToolExecutionContext context);
}

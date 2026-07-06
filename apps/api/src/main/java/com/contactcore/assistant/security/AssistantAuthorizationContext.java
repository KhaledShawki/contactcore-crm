// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.tool.AssistantToolCall;
import com.contactcore.iam.evaluation.IamRequestContext;
import java.util.LinkedHashMap;
import java.util.Map;

public record AssistantAuthorizationContext(
        Long conversationId,
        String toolName,
        String toolCategory,
        String query,
        String retrievalType,
        String operation
) {
    public static AssistantAuthorizationContext forAsk(Long conversationId, String message) {
        return new AssistantAuthorizationContext(conversationId, null, null, normalize(message), null, "ask");
    }

    public static AssistantAuthorizationContext forConversation(Long conversationId, String operation) {
        return new AssistantAuthorizationContext(conversationId, null, null, null, null, operation);
    }

    public static AssistantAuthorizationContext forTool(AssistantToolCall call,
                                                        AssistantToolCategory category,
                                                        AssistantRetrievalType retrievalType) {
        return new AssistantAuthorizationContext(
                null,
                call == null ? null : call.toolName(),
                category == null ? null : category.resourceSegment(),
                call == null ? null : firstNonBlank(call.stringArgument("query"), call.stringArgument("source"), call.stringArgument("kindCode")),
                retrievalType == null ? null : retrievalType.name(),
                "executeTool"
        );
    }

    public IamRequestContext toRequestContext() {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "conversationId", conversationId);
        put(values, "toolName", toolName);
        put(values, "toolCategory", toolCategory);
        put(values, "query", query);
        put(values, "retrievalType", retrievalType);
        put(values, "operation", operation);
        return new IamRequestContext(values);
    }

    private static void put(Map<String, Object> values, String key, Object value) {
        if (value != null) {
            values.put(key, value);
        }
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        return normalized.length() <= 120 ? normalized : normalized.substring(0, 120);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            String normalized = normalize(value);
            if (normalized != null) {
                return normalized;
            }
        }
        return null;
    }
}

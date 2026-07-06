// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.iam.domain.IamResource;

public final class AssistantIamResources {
    private AssistantIamResources() {}

    public static IamResource sessions(String tenantId) {
        return IamResource.contactCore(tenantId, AssistantIamActions.SERVICE, "session", "*");
    }

    public static IamResource conversations(String tenantId) {
        return IamResource.contactCore(tenantId, AssistantIamActions.SERVICE, "conversation", "*");
    }

    public static IamResource conversation(String tenantId, Long conversationId) {
        return IamResource.contactCore(tenantId, AssistantIamActions.SERVICE, "conversation", String.valueOf(conversationId));
    }

    public static IamResource crmTools(String tenantId) {
        return tools(tenantId, "crm/*");
    }

    public static IamResource commercialTools(String tenantId) {
        return tools(tenantId, "commercial/*");
    }

    public static IamResource connectorTools(String tenantId) {
        return tools(tenantId, "connector/*");
    }

    public static IamResource schemaTools(String tenantId) {
        return tools(tenantId, "schema/*");
    }

    public static IamResource reportTools(String tenantId) {
        return tools(tenantId, "report/*");
    }

    public static IamResource tool(String tenantId, String toolCategory, String toolName) {
        return tools(tenantId, required(toolCategory, "toolCategory") + "/" + required(toolName, "toolName"));
    }

    private static IamResource tools(String tenantId, String resourceId) {
        return IamResource.contactCore(tenantId, AssistantIamActions.SERVICE, "tool", required(resourceId, "resourceId"));
    }

    private static String required(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamResource;
import java.util.Objects;
import java.util.function.Function;

public record AssistantToolAuthorizationRule(
        String toolName,
        AssistantToolCategory category,
        IamAction moduleAction,
        Function<String, IamResource> moduleResourceFactory
) {
    public AssistantToolAuthorizationRule {
        toolName = Objects.requireNonNull(toolName, "toolName must not be null").trim();
        if (toolName.isBlank()) {
            throw new IllegalArgumentException("toolName must not be blank");
        }
        category = Objects.requireNonNull(category, "category must not be null");
        moduleAction = Objects.requireNonNull(moduleAction, "moduleAction must not be null");
        moduleResourceFactory = Objects.requireNonNull(moduleResourceFactory, "moduleResourceFactory must not be null");
    }

    public IamResource assistantResource(String tenantId) {
        return AssistantIamResources.tool(tenantId, category.resourceSegment(), toolName);
    }

    public IamResource moduleResource(String tenantId) {
        return moduleResourceFactory.apply(tenantId);
    }
}

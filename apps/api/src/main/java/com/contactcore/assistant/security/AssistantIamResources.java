// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.iam.domain.IamResource;

public final class AssistantIamResources {
    private AssistantIamResources() {}

    public static IamResource sessions(String tenantId) {
        return IamResource.contactCore(tenantId, AssistantIamActions.SERVICE, "session", "*");
    }

    public static IamResource commercialTools(String tenantId) {
        return IamResource.contactCore(tenantId, AssistantIamActions.SERVICE, "tool", "commercial/*");
    }
}

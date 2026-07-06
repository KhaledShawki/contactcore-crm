// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.security;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamResource;
import java.util.function.Function;

public enum AssistantToolCategory {
    CRM("crm", AssistantIamActions.USE_CRM_TOOLS, AssistantIamResources::crmTools),
    COMMERCIAL("commercial", AssistantIamActions.USE_COMMERCIAL_TOOLS, AssistantIamResources::commercialTools),
    CONNECTOR("connector", AssistantIamActions.USE_CONNECTOR_TOOLS, AssistantIamResources::connectorTools),
    SCHEMA("schema", AssistantIamActions.USE_SCHEMA_TOOLS, AssistantIamResources::schemaTools),
    REPORT("report", AssistantIamActions.USE_REPORT_TOOLS, AssistantIamResources::reportTools);

    private final String resourceSegment;
    private final IamAction action;
    private final Function<String, IamResource> collectionResourceFactory;

    AssistantToolCategory(String resourceSegment, IamAction action, Function<String, IamResource> collectionResourceFactory) {
        this.resourceSegment = resourceSegment;
        this.action = action;
        this.collectionResourceFactory = collectionResourceFactory;
    }

    public String resourceSegment() {
        return resourceSegment;
    }

    public IamAction action() {
        return action;
    }

    public IamResource collectionResource(String tenantId) {
        return collectionResourceFactory.apply(tenantId);
    }
}

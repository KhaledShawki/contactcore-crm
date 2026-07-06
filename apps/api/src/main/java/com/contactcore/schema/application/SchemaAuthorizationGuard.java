// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.application;

import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.schema.security.SchemaIamActions;
import com.contactcore.schema.security.SchemaIamResources;
import org.springframework.stereotype.Component;

@Component
public class SchemaAuthorizationGuard {
    private final CurrentIamAuthorizationService authorization;
    private final ContactCoreTenantContext tenantContext;

    public SchemaAuthorizationGuard(CurrentIamAuthorizationService authorization, ContactCoreTenantContext tenantContext) {
        this.authorization = authorization;
        this.tenantContext = tenantContext;
    }

    public void requireReadManifest() {
        authorization.requireAllowed(SchemaIamActions.READ_MANIFEST, SchemaIamResources.manifest(tenantContext.currentTenantId()));
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.security;

import com.contactcore.iam.domain.IamResource;

public final class SchemaIamResources {
    private SchemaIamResources() {}

    public static IamResource manifest(String tenantId) {
        return IamResource.contactCore(tenantId, SchemaIamActions.SERVICE, "manifest", "app");
    }
}

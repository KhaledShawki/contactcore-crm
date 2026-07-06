// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.security;

import com.contactcore.iam.domain.IamResource;

public final class CommercialIamResources {
    private CommercialIamResources() {}

    public static IamResource documents(String tenantId) {
        return IamResource.contactCore(tenantId, CommercialIamActions.SERVICE, "document", "*");
    }

    public static IamResource document(String tenantId, Long id) {
        return IamResource.contactCore(tenantId, CommercialIamActions.SERVICE, "document", String.valueOf(id));
    }

    public static IamResource items(String tenantId) {
        return IamResource.contactCore(tenantId, CommercialIamActions.SERVICE, "item", "*");
    }

    public static IamResource item(String tenantId, Long id) {
        return IamResource.contactCore(tenantId, CommercialIamActions.SERVICE, "item", String.valueOf(id));
    }
}

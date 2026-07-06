// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.security;

import com.contactcore.iam.domain.IamResource;

public final class IamManagementResources {
    private IamManagementResources() {}

    public static IamResource policies(String tenantId) {
        return IamResource.contactCore(tenantId, IamManagementActions.SERVICE, "policy", "*");
    }

    public static IamResource roles(String tenantId) {
        return IamResource.contactCore(tenantId, IamManagementActions.SERVICE, "role", "*");
    }
}

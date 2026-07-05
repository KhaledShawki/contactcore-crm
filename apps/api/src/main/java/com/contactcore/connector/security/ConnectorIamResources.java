// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.security;

import com.contactcore.iam.domain.IamResource;

public final class ConnectorIamResources {
    private ConnectorIamResources() {}

    public static IamResource instances(String tenantId) {
        return IamResource.contactCore(tenantId, ConnectorIamActions.SERVICE, "instance", "*");
    }
}

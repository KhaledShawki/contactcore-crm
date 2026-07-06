// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.security;

import com.contactcore.iam.domain.IamResource;

public final class ConnectorIamResources {
    private ConnectorIamResources() {}

    public static IamResource instances(String tenantId) {
        return IamResource.contactCore(tenantId, ConnectorIamActions.SERVICE, "instance", "*");
    }

    public static IamResource instance(String tenantId, Long instanceId) {
        return IamResource.contactCore(tenantId, ConnectorIamActions.SERVICE, "instance", String.valueOf(instanceId));
    }

    public static IamResource sessions(String tenantId) {
        return IamResource.contactCore(tenantId, ConnectorIamActions.SERVICE, "session", "*");
    }

    public static IamResource currentSession(String tenantId) {
        return IamResource.contactCore(tenantId, ConnectorIamActions.SERVICE, "session", "current");
    }

    public static IamResource businessPartners(String tenantId) {
        return IamResource.contactCore(tenantId, ConnectorIamActions.SERVICE, "business-partner", "*");
    }

    public static IamResource businessPartner(String tenantId, String externalId) {
        return IamResource.contactCore(tenantId, ConnectorIamActions.SERVICE, "business-partner", externalId);
    }
}

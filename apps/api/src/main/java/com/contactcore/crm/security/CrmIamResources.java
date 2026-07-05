// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.security;

import com.contactcore.iam.domain.IamResource;

public final class CrmIamResources {
    private CrmIamResources() {}

    public static IamResource businessPartners(String tenantId) {
        return IamResource.contactCore(tenantId, CrmIamActions.SERVICE, "business-partner", "*");
    }

    public static IamResource businessPartner(String tenantId, Long id) {
        return IamResource.contactCore(tenantId, CrmIamActions.SERVICE, "business-partner", String.valueOf(id));
    }
}

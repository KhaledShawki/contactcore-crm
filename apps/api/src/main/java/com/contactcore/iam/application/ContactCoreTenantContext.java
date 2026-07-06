// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import org.springframework.stereotype.Component;

@Component
public class ContactCoreTenantContext {
    public static final String DEFAULT_TENANT_ID = "default";

    public String currentTenantId() {
        return DEFAULT_TENANT_ID;
    }
}

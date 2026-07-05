// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.security;

import com.contactcore.iam.application.ProductCapabilityCatalog;
import com.contactcore.iam.evaluation.ProductCapabilityRule;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SchemaProductCapabilityCatalog implements ProductCapabilityCatalog {
    @Override
    public String service() {
        return SchemaIamActions.SERVICE;
    }

    @Override
    public List<ProductCapabilityRule> rulesForTenant(String tenantId) {
        return List.of(new ProductCapabilityRule(SchemaIamActions.READ_MANIFEST, SchemaIamResources.manifest(tenantId)));
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.security;

import com.contactcore.iam.application.ProductCapabilityCatalog;
import com.contactcore.iam.evaluation.ProductCapabilityRule;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CommercialProductCapabilityCatalog implements ProductCapabilityCatalog {
    @Override
    public String service() {
        return CommercialIamActions.SERVICE;
    }

    @Override
    public List<ProductCapabilityRule> rulesForTenant(String tenantId) {
        return List.of(
                new ProductCapabilityRule(CommercialIamActions.LIST_DOCUMENTS, CommercialIamResources.documents(tenantId)),
                new ProductCapabilityRule(CommercialIamActions.READ_DOCUMENT, CommercialIamResources.documents(tenantId)),
                new ProductCapabilityRule(CommercialIamActions.EXPORT_DOCUMENTS, CommercialIamResources.documents(tenantId)),
                new ProductCapabilityRule(CommercialIamActions.SYNC_DOCUMENTS, CommercialIamResources.documents(tenantId)),
                new ProductCapabilityRule(CommercialIamActions.LIST_ITEMS, CommercialIamResources.items(tenantId)),
                new ProductCapabilityRule(CommercialIamActions.READ_ITEM, CommercialIamResources.items(tenantId)),
                new ProductCapabilityRule(CommercialIamActions.SYNC_ITEMS, CommercialIamResources.items(tenantId))
        );
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.security;

import com.contactcore.iam.application.ProductCapabilityCatalog;
import com.contactcore.iam.evaluation.ProductCapabilityRule;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StorageProductCapabilityCatalog implements ProductCapabilityCatalog {
    @Override
    public String service() {
        return StorageIamActions.SERVICE;
    }

    @Override
    public List<ProductCapabilityRule> rulesForTenant(String tenantId) {
        return List.of(
                new ProductCapabilityRule(StorageIamActions.READ_OBJECT, StorageIamResources.objects(tenantId)),
                new ProductCapabilityRule(StorageIamActions.UPLOAD_OBJECT, StorageIamResources.objects(tenantId)),
                new ProductCapabilityRule(StorageIamActions.DOWNLOAD_OBJECT, StorageIamResources.objects(tenantId)),
                new ProductCapabilityRule(StorageIamActions.PREVIEW_OBJECT, StorageIamResources.objects(tenantId)),
                new ProductCapabilityRule(StorageIamActions.DELETE_OBJECT, StorageIamResources.objects(tenantId))
        );
    }
}

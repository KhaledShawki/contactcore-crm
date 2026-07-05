// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.evaluation.ProductCapabilityRule;
import java.util.List;

public interface ProductCapabilityCatalog {
    String service();

    List<ProductCapabilityRule> rulesForTenant(String tenantId);
}

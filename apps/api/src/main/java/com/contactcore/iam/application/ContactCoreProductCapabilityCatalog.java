// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.evaluation.ProductCapabilityBoundary;
import com.contactcore.iam.evaluation.ProductCapabilityRule;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ContactCoreProductCapabilityCatalog {
    private final List<ProductCapabilityCatalog> catalogs;

    public ContactCoreProductCapabilityCatalog(List<ProductCapabilityCatalog> catalogs) {
        this.catalogs = catalogs == null ? List.of() : catalogs.stream()
                .map(catalog -> Objects.requireNonNull(catalog, "catalog must not be null"))
                .sorted(Comparator.comparing(ProductCapabilityCatalog::service))
                .toList();
    }

    public ProductCapabilityBoundary boundaryForTenant(String tenantId) {
        return new ProductCapabilityBoundary(catalogs.stream()
                .flatMap(catalog -> rulesForTenant(catalog, tenantId).stream())
                .toList());
    }

    private List<ProductCapabilityRule> rulesForTenant(ProductCapabilityCatalog catalog, String tenantId) {
        List<ProductCapabilityRule> rules = catalog.rulesForTenant(tenantId);
        if (rules == null) {
            throw new IllegalStateException("Product capability catalog returned null rules: " + catalog.service());
        }
        return rules.stream()
                .map(rule -> Objects.requireNonNull(rule, "product capability rule must not be null"))
                .toList();
    }
}

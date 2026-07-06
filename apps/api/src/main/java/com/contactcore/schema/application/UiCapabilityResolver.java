// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.application;

import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.iam.application.CurrentIamSubject;
import com.contactcore.schema.api.UiResourceCapabilities;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UiCapabilityResolver {
    private final CurrentIamAuthorizationService authorization;
    private final ContactCoreTenantContext tenantContext;
    private final UiCapabilityCatalog capabilityCatalog;

    public UiCapabilityResolver(CurrentIamAuthorizationService authorization, ContactCoreTenantContext tenantContext,
                                UiCapabilityCatalog capabilityCatalog) {
        this.authorization = authorization;
        this.tenantContext = tenantContext;
        this.capabilityCatalog = capabilityCatalog;
    }

    public UiCapabilitySnapshot resolveCurrentSubjectCapabilities() {
        return resolveCapabilities(authorization.currentSubject(), tenantContext.currentTenantId());
    }

    public UiCapabilitySnapshot resolveCapabilities(CurrentIamSubject subject, String tenantId) {
        List<UiCapabilityDefinition> definitions = capabilityCatalog.definitionsForTenant(tenantId);
        Map<String, Map<String, Boolean>> capabilities = new LinkedHashMap<>();
        for (UiCapabilityDefinition definition : definitions) {
            boolean allowed = authorization.evaluate(subject, definition.action(), definition.resource()).allowed();
            capabilities.computeIfAbsent(definition.resourceKey(), ignored -> new LinkedHashMap<>())
                    .put(definition.capability(), allowed);
        }
        return new UiCapabilitySnapshot(capabilities.entrySet().stream()
                .map(entry -> new UiResourceCapabilities(entry.getKey(), entry.getValue()))
                .toList());
    }
}

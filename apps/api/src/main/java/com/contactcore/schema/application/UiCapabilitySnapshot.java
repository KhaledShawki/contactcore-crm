// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.application;

import com.contactcore.schema.api.UiCapabilityReference;
import com.contactcore.schema.api.UiResourceCapabilities;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class UiCapabilitySnapshot {
    private final Map<String, UiResourceCapabilities> capabilitiesByResource;

    public UiCapabilitySnapshot(Collection<UiResourceCapabilities> capabilities) {
        Map<String, UiResourceCapabilities> copied = new LinkedHashMap<>();
        if (capabilities != null) {
            capabilities.stream()
                    .map(item -> Objects.requireNonNull(item, "capability set must not be null"))
                    .sorted(Comparator.comparing(UiResourceCapabilities::resourceKey))
                    .forEach(item -> copied.put(item.resourceKey(), item));
        }
        this.capabilitiesByResource = Map.copyOf(copied);
    }

    public static UiCapabilitySnapshot empty() {
        return new UiCapabilitySnapshot(List.of());
    }

    public List<UiResourceCapabilities> asList() {
        return capabilitiesByResource.values().stream()
                .sorted(Comparator.comparing(UiResourceCapabilities::resourceKey))
                .toList();
    }

    public UiResourceCapabilities resource(String resourceKey) {
        return find(resourceKey).orElseGet(() -> UiResourceCapabilities.empty(resourceKey));
    }

    public Optional<UiResourceCapabilities> find(String resourceKey) {
        if (resourceKey == null || resourceKey.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(capabilitiesByResource.get(resourceKey.trim()));
    }

    public boolean allows(UiCapabilityReference reference) {
        if (reference == null) {
            return true;
        }
        return find(reference.resourceKey())
                .map(capabilitySet -> capabilitySet.allows(reference.capability()))
                .orElse(false);
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

public record ConnectorBusinessPartnerStatus(
        boolean active,
        boolean frozen,
        boolean valid,
        String lifecycleStatus,
        String sourceStatusCode
) {
    public ConnectorBusinessPartnerStatus {
        lifecycleStatus = lifecycleStatus == null || lifecycleStatus.isBlank()
                ? defaultLifecycle(active, frozen, valid)
                : lifecycleStatus.trim();
        sourceStatusCode = sourceStatusCode == null || sourceStatusCode.isBlank() ? null : sourceStatusCode.trim();
    }

    public String displayName() {
        return switch (lifecycleStatus) {
            case "FROZEN" -> "Frozen";
            case "INACTIVE" -> "Inactive";
            case "INVALID" -> "Invalid";
            default -> "Active";
        };
    }

    private static String defaultLifecycle(boolean active, boolean frozen, boolean valid) {
        if (frozen) {
            return "FROZEN";
        }
        if (!valid) {
            return "INVALID";
        }
        return active ? "ACTIVE" : "INACTIVE";
    }
}

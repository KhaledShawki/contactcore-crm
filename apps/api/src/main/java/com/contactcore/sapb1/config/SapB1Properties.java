// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.config;

import com.contactcore.connector.model.CrmConnectorEnvironment;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "contactcore.sap-b1")
public record SapB1Properties(
        boolean enabled,
        String displayName,
        CrmConnectorEnvironment environment,
        String serviceLayerBaseUrl,
        String companyDb,
        @Min(1000) int timeoutMs,
        List<String> grantUsernames
) {
    public boolean hasConnectionConfig() {
        return hasText(serviceLayerBaseUrl) && hasText(companyDb);
    }

    public String normalizedDisplayName() {
        return hasText(displayName) ? displayName.trim() : "SAP Business One";
    }

    public CrmConnectorEnvironment normalizedEnvironment() {
        return environment == null ? CrmConnectorEnvironment.TEST : environment;
    }

    public String normalizedServiceLayerBaseUrl() {
        String normalized = serviceLayerBaseUrl == null ? "" : serviceLayerBaseUrl.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public String normalizedCompanyDb() {
        return companyDb == null ? "" : companyDb.trim();
    }

    public List<String> normalizedGrantUsernames() {
        if (grantUsernames == null) {
            return List.of();
        }
        return grantUsernames.stream()
                .filter(SapB1Properties::hasText)
                .map(String::trim)
                .distinct()
                .toList();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

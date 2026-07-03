// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

public record ConnectorBusinessPartnerSourceReference(
        String connectorType,
        Long connectorInstanceId,
        String connectorDisplayName,
        String resourceType,
        String externalId,
        String displayReference
) {
    public ConnectorBusinessPartnerSourceReference {
        connectorType = blankToNull(connectorType);
        connectorDisplayName = blankToNull(connectorDisplayName);
        resourceType = blankToNull(resourceType);
        externalId = blankToNull(externalId);
        displayReference = blankToNull(displayReference);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

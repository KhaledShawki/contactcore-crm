// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

public record ConnectorBusinessPartnerIdentity(
        Long connectorInstanceId,
        String connectorType,
        String externalId,
        String code,
        String displayName,
        ConnectorBusinessPartnerSourceReference source
) {
    public ConnectorBusinessPartnerIdentity {
        connectorType = required(connectorType, "connectorType");
        externalId = required(externalId, "externalId");
        code = blankToNull(code) == null ? externalId : code.trim();
        displayName = required(displayName, "displayName");
    }

    private static String required(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value.trim();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

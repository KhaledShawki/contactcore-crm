// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

public record ConnectorBusinessPartnerContactPoint(
        ConnectorContactPointType type,
        String label,
        String value,
        boolean primary,
        boolean verified,
        ConnectorBusinessPartnerSourceReference source
) {
    public ConnectorBusinessPartnerContactPoint {
        type = type == null ? ConnectorContactPointType.OTHER : type;
        label = label == null || label.isBlank() ? null : label.trim();
        value = value == null || value.isBlank() ? null : value.trim();
    }
}

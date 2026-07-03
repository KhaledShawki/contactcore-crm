// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.model;

import java.util.Locale;

public enum CrmConnectorType {
    SAP_B1;

    public static CrmConnectorType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Connector type must not be blank.");
        }
        return CrmConnectorType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}

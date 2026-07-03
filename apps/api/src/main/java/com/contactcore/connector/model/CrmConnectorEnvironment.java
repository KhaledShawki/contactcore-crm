// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.model;

import java.util.Locale;

public enum CrmConnectorEnvironment {
    DEV,
    TEST,
    PROD;

    public static CrmConnectorEnvironment from(String value) {
        if (value == null || value.isBlank()) {
            return TEST;
        }
        return CrmConnectorEnvironment.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}

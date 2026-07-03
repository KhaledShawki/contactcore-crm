// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.model;

import java.util.Locale;

public enum CrmBusinessPartnerType {
    CUSTOMER,
    SUPPLIER,
    LEAD;

    public static CrmBusinessPartnerType optional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return CrmBusinessPartnerType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}

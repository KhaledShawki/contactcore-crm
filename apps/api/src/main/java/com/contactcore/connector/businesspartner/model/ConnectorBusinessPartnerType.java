// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

import com.contactcore.connector.model.CrmBusinessPartnerType;
import java.util.Locale;

public enum ConnectorBusinessPartnerType {
    CUSTOMER,
    SUPPLIER,
    LEAD,
    UNKNOWN;

    public static ConnectorBusinessPartnerType fromBusinessPartnerType(CrmBusinessPartnerType type) {
        return type == null ? null : switch (type) {
            case CUSTOMER -> CUSTOMER;
            case SUPPLIER -> SUPPLIER;
            case LEAD -> LEAD;
        };
    }

    public static ConnectorBusinessPartnerType optional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return ConnectorBusinessPartnerType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    public String displayName() {
        return switch (this) {
            case CUSTOMER -> "Customer";
            case SUPPLIER -> "Supplier";
            case LEAD -> "Lead";
            case UNKNOWN -> "Unknown";
        };
    }
}

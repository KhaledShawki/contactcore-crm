// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

import java.util.Locale;

public enum CommercialDashboardGroupBy {
    MONTH;

    public static CommercialDashboardGroupBy from(String value) {
        if (value == null || value.isBlank()) {
            return MONTH;
        }
        return CommercialDashboardGroupBy.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}

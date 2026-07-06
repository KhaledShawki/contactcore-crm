// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.security;

import com.contactcore.dashboard.application.CommercialDashboardQuery;
import com.contactcore.iam.evaluation.IamRequestContext;
import java.util.LinkedHashMap;
import java.util.Map;

public record DashboardAuthorizationContext(
        String dashboard,
        String widget,
        String operation,
        String from,
        String to,
        String currency,
        Integer limit
) {
    public static DashboardAuthorizationContext forCommercialWidget(String widget, CommercialDashboardQuery query) {
        return new DashboardAuthorizationContext(
                "commercial",
                widget,
                "read",
                query.dateRange().from().toString(),
                query.dateRange().to().toString(),
                query.currency(),
                query.limit()
        );
    }

    public IamRequestContext toRequestContext() {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "dashboard", dashboard);
        put(values, "widget", widget);
        put(values, "operation", operation);
        put(values, "from", from);
        put(values, "to", to);
        put(values, "currency", currency);
        put(values, "limit", limit);
        return new IamRequestContext(values);
    }

    private static void put(Map<String, Object> values, String key, Object value) {
        if (value != null) {
            values.put(key, value);
        }
    }
}

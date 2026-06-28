// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.analytics.api;

public record KpiResponse(
        String key,
        String label,
        double value,
        String unit,
        String helpText
) {}

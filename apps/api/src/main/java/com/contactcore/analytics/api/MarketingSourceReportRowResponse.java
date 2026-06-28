// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.analytics.api;

public record MarketingSourceReportRowResponse(
        String marketingSource,
        long leads,
        long qualifiedLeads,
        long customers,
        double leadQualificationRate
) {}

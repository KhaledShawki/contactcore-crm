// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.marketing;

import java.time.Instant;

public record MarketingSourceReportRow(
        String code,
        String name,
        Integer sortOrder,
        Integer businessPartners,
        Integer leads,
        Integer customers,
        Integer suppliers,
        Instant createdAt,
        Instant updatedAt
) {}

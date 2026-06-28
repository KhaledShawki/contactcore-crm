// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.marketing.api;

import java.time.Instant;

public record MarketingSourceResponse(
        Long id,
        Long version,
        Instant createdAt,
        Instant updatedAt,
        String code,
        String name,
        Integer sortOrder
) {}

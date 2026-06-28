// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.analytics.api;

import java.time.Instant;

public record RecentBusinessPartnerResponse(
        Long id,
        String kind,
        String code,
        String name,
        String status,
        String marketingSource,
        Instant createdAt
) {}

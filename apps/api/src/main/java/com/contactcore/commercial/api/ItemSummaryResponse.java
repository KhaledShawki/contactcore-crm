// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.api;

import java.time.Instant;

public record ItemSummaryResponse(
        Long id,
        Long version,
        Instant createdAt,
        Instant updatedAt,
        String sourceSystem,
        String sourceTenantId,
        String externalId,
        String itemCode,
        String name,
        String itemGroup,
        String unitOfMeasure,
        boolean active,
        Instant lastSyncedAt
) {}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.api;

import java.math.BigDecimal;

public record InvoiceAgingBucketResponse(
        String bucket,
        BigDecimal openAmount,
        String currency
) {}

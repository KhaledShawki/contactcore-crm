// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.api;

import java.math.BigDecimal;

public record TopSellingItemResponse(
        String itemCode,
        String itemName,
        BigDecimal quantitySold,
        BigDecimal netAmount,
        String currency
) {}

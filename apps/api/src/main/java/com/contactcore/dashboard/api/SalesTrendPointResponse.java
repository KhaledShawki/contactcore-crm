// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.api;

import java.math.BigDecimal;

public record SalesTrendPointResponse(
        String period,
        BigDecimal netAmount,
        String currency
) {}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.api;

import java.math.BigDecimal;

public record CommercialAmountByCurrencyResponse(
        String currency,
        long documentCount,
        BigDecimal totalAmount,
        BigDecimal openAmount
) {}

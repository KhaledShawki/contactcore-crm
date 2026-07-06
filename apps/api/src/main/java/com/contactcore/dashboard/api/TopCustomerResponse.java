// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.api;

import java.math.BigDecimal;

public record TopCustomerResponse(
        String businessPartnerCode,
        String businessPartnerName,
        BigDecimal netAmount,
        String currency
) {}

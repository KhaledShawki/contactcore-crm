// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.api;

import java.math.BigDecimal;

public record SalesByDocumentTypeResponse(
        String documentType,
        BigDecimal netAmount,
        String currency
) {}

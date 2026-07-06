// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

import java.math.BigDecimal;

public record CommercialDashboardLine(
        String itemCode,
        String itemName,
        BigDecimal quantity,
        BigDecimal netAmount
) {
    public CommercialDashboardLine {
        itemCode = normalize(itemCode);
        itemName = normalize(itemName);
        quantity = quantity == null ? BigDecimal.ZERO : quantity;
        netAmount = netAmount == null ? BigDecimal.ZERO : netAmount;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}

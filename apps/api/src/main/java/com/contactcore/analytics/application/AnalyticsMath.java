// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.analytics.application;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AnalyticsMath {
    private AnalyticsMath() {}

    public static double percentage(long part, long total) {
        if (part <= 0 || total <= 0) {
            return 0.0;
        }
        return BigDecimal.valueOf(part)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP)
                .doubleValue();
    }
}

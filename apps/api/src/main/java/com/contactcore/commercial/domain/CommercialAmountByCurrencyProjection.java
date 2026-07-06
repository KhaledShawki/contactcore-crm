// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.domain;

import java.math.BigDecimal;

public interface CommercialAmountByCurrencyProjection {
    String getCurrency();

    Long getDocumentCount();

    BigDecimal getTotalAmount();

    BigDecimal getOpenAmount();
}

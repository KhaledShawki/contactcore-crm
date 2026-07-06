// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.api;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CommercialDocumentLineResponse(
        Long id,
        Long version,
        String sourceLineId,
        Integer lineNumber,
        Long itemId,
        String itemExternalId,
        String itemCodeSnapshot,
        String itemNameSnapshot,
        String description,
        BigDecimal quantity,
        BigDecimal openQuantity,
        String unitOfMeasure,
        BigDecimal unitPrice,
        BigDecimal discountPercent,
        String taxCodeSnapshot,
        BigDecimal lineTotal,
        String currency,
        LocalDate deliveryDate
) {}

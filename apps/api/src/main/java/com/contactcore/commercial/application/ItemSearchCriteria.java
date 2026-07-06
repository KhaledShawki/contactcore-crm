// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import com.contactcore.commercial.domain.CommercialSourceSystem;

public record ItemSearchCriteria(
        CommercialSourceSystem sourceSystem,
        Boolean active,
        String query
) {
    public boolean hasQuery() {
        return query != null && !query.isBlank();
    }
}

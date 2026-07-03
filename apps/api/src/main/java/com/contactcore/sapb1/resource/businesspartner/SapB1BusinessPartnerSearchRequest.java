// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.businesspartner;

import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerType;
import com.contactcore.connector.businesspartner.query.ConnectorBusinessPartnerSearchCriteria;
import com.contactcore.connector.businesspartner.query.ConnectorBusinessPartnerSort;

public record SapB1BusinessPartnerSearchRequest(
        String query,
        ConnectorBusinessPartnerType type,
        int page,
        int size,
        ConnectorBusinessPartnerSort sort
) {
    public SapB1BusinessPartnerSearchRequest {
        query = query == null ? "" : query.trim();
        page = Math.max(0, page);
        size = Math.max(1, Math.min(size <= 0 ? 20 : size, 100));
        sort = sort == null ? ConnectorBusinessPartnerSort.CODE_ASC : sort;
    }

    public static SapB1BusinessPartnerSearchRequest from(ConnectorBusinessPartnerSearchCriteria criteria) {
        return new SapB1BusinessPartnerSearchRequest(criteria.query(), criteria.type(), criteria.page(), criteria.size(), criteria.sort());
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.query;

import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerType;
import com.contactcore.connector.model.CrmBusinessPartnerSearchCriteria;

public record ConnectorBusinessPartnerSearchCriteria(
        String query,
        ConnectorBusinessPartnerType type,
        ConnectorBusinessPartnerStatusFilter status,
        int page,
        int size,
        ConnectorBusinessPartnerSort sort
) {
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    public ConnectorBusinessPartnerSearchCriteria {
        query = query == null ? "" : query.trim();
        status = status == null ? ConnectorBusinessPartnerStatusFilter.ANY : status;
        page = Math.max(0, page);
        size = Math.max(1, Math.min(size <= 0 ? DEFAULT_SIZE : size, MAX_SIZE));
        sort = sort == null ? ConnectorBusinessPartnerSort.CODE_ASC : sort;
    }

    public static ConnectorBusinessPartnerSearchCriteria from(CrmBusinessPartnerSearchCriteria criteria) {
        return new ConnectorBusinessPartnerSearchCriteria(
                criteria.query(),
                ConnectorBusinessPartnerType.fromBusinessPartnerType(criteria.type()),
                ConnectorBusinessPartnerStatusFilter.ANY,
                criteria.page(),
                criteria.size(),
                ConnectorBusinessPartnerSort.from(criteria.sort())
        );
    }
}

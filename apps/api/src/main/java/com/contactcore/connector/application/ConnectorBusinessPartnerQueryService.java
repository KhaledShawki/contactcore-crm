// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.application;

import com.contactcore.connector.businesspartner.application.ConnectorBusinessPartnerReadService;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerDetail;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerSummary;
import com.contactcore.connector.businesspartner.query.ConnectorBusinessPartnerSearchCriteria;
import com.contactcore.connector.model.CrmBusinessPartnerSearchCriteria;
import com.contactcore.connector.model.CrmBusinessPartnerView;
import com.contactcore.shared.api.PageResponse;
import org.springframework.stereotype.Service;

@Service
public class ConnectorBusinessPartnerQueryService {
    private final ConnectorBusinessPartnerReadService businessPartners;

    public ConnectorBusinessPartnerQueryService(ConnectorBusinessPartnerReadService businessPartners) {
        this.businessPartners = businessPartners;
    }

    public PageResponse<CrmBusinessPartnerView> search(Long userId, CrmBusinessPartnerSearchCriteria criteria) {
        PageResponse<ConnectorBusinessPartnerSummary> result = businessPartners.search(
                userId,
                ConnectorBusinessPartnerSearchCriteria.from(criteria)
        );
        return new PageResponse<>(
                result.items().stream().map(CrmBusinessPartnerView::fromConnectorBusinessPartnerSummary).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    public CrmBusinessPartnerView get(Long userId, String externalId) {
        ConnectorBusinessPartnerDetail detail = businessPartners.get(userId, externalId);
        return CrmBusinessPartnerView.fromConnectorBusinessPartnerDetail(detail);
    }
}

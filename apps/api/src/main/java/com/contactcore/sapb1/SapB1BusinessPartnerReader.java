// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1;

import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerDetail;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerSummary;
import com.contactcore.connector.businesspartner.port.ConnectorBusinessPartnerReader;
import com.contactcore.connector.businesspartner.query.ConnectorBusinessPartnerSearchCriteria;
import com.contactcore.connector.port.ConnectorExecutionContext;
import com.contactcore.sapb1.resource.businesspartner.SapB1BusinessPartnerGateway;
import com.contactcore.sapb1.resource.businesspartner.SapB1BusinessPartnerMapper;
import com.contactcore.sapb1.resource.businesspartner.SapB1BusinessPartnerSearchRequest;
import com.contactcore.shared.api.PageResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SapB1BusinessPartnerReader implements ConnectorBusinessPartnerReader {
    private final SapB1BusinessPartnerGateway gateway;
    private final SapB1BusinessPartnerMapper mapper;

    public SapB1BusinessPartnerReader(SapB1BusinessPartnerGateway gateway, SapB1BusinessPartnerMapper mapper) {
        this.gateway = gateway;
        this.mapper = mapper;
    }

    @Override
    public PageResponse<ConnectorBusinessPartnerSummary> search(ConnectorExecutionContext context, ConnectorBusinessPartnerSearchCriteria criteria) {
        List<ConnectorBusinessPartnerSummary> items = gateway.search(context, SapB1BusinessPartnerSearchRequest.from(criteria)).value().stream()
                .map(dto -> mapper.toSummary(context.instance(), dto))
                .toList();
        long estimatedTotal = (long) criteria.page() * criteria.size() + items.size();
        int totalPages = items.size() < criteria.size() ? criteria.page() + 1 : criteria.page() + 2;
        return new PageResponse<>(items, criteria.page(), criteria.size(), estimatedTotal, Math.max(1, totalPages));
    }

    @Override
    public Optional<ConnectorBusinessPartnerDetail> findByExternalId(ConnectorExecutionContext context, String externalId) {
        if (externalId == null || externalId.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(gateway.findByCardCode(context, externalId.trim()))
                .map(dto -> mapper.toDetail(context.instance(), dto));
    }
}

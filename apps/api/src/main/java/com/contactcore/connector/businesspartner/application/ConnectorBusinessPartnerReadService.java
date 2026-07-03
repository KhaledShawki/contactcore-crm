// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.application;

import com.contactcore.connector.application.ConnectorRegistry;
import com.contactcore.connector.application.ConnectorSessionService;
import com.contactcore.connector.application.ConnectorSessionState;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerDetail;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerSummary;
import com.contactcore.connector.businesspartner.query.ConnectorBusinessPartnerSearchCriteria;
import com.contactcore.connector.model.CrmConnectorCapability;
import com.contactcore.connector.port.ConnectorExecutionContext;
import com.contactcore.connector.port.CrmConnector;
import com.contactcore.shared.api.NotFoundException;
import com.contactcore.shared.api.PageResponse;
import org.springframework.stereotype.Service;

@Service
public class ConnectorBusinessPartnerReadService {
    private final ConnectorSessionService sessions;
    private final ConnectorRegistry registry;

    public ConnectorBusinessPartnerReadService(ConnectorSessionService sessions, ConnectorRegistry registry) {
        this.sessions = sessions;
        this.registry = registry;
    }

    public PageResponse<ConnectorBusinessPartnerSummary> search(Long userId, ConnectorBusinessPartnerSearchCriteria criteria) {
        ConnectorSessionState session = sessions.requireActiveSession(userId, CrmConnectorCapability.READ_BUSINESS_PARTNERS);
        CrmConnector connector = registry.require(session.instance().getType());
        return connector.businessPartners().search(context(userId, session), criteria);
    }

    public ConnectorBusinessPartnerDetail get(Long userId, String externalId) {
        ConnectorSessionState session = sessions.requireActiveSession(userId, CrmConnectorCapability.READ_BUSINESS_PARTNERS);
        CrmConnector connector = registry.require(session.instance().getType());
        return connector.businessPartners().findByExternalId(context(userId, session), externalId)
                .orElseThrow(() -> new NotFoundException("CRM connector business partner not found: " + externalId));
    }

    private ConnectorExecutionContext context(Long userId, ConnectorSessionState session) {
        return new ConnectorExecutionContext(userId, session.instance(), session.adapterSession());
    }
}

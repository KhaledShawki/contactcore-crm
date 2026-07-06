// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1;

import com.contactcore.connector.businesspartner.port.ConnectorBusinessPartnerReader;
import com.contactcore.connector.model.CrmConnectorCapability;
import com.contactcore.connector.model.CrmConnectorType;
import com.contactcore.connector.port.CrmConnector;
import com.contactcore.connector.port.CrmConnectorSessionProvider;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class SapB1Connector implements CrmConnector {
    private static final Set<CrmConnectorCapability> CAPABILITIES = Set.of(
            CrmConnectorCapability.READ_BUSINESS_PARTNERS,
            CrmConnectorCapability.READ_DOCUMENTS
    );

    private final SapB1SessionProvider sessionProvider;
    private final SapB1BusinessPartnerReader businessPartnerReader;

    public SapB1Connector(SapB1SessionProvider sessionProvider, SapB1BusinessPartnerReader businessPartnerReader) {
        this.sessionProvider = sessionProvider;
        this.businessPartnerReader = businessPartnerReader;
    }

    @Override
    public CrmConnectorType type() {
        return CrmConnectorType.SAP_B1;
    }

    @Override
    public Set<CrmConnectorCapability> capabilities() {
        return CAPABILITIES;
    }

    @Override
    public CrmConnectorSessionProvider sessions() {
        return sessionProvider;
    }

    @Override
    public ConnectorBusinessPartnerReader businessPartners() {
        return businessPartnerReader;
    }
}

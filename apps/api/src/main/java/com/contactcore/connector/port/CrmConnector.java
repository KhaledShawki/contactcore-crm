// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.port;

import com.contactcore.connector.businesspartner.port.ConnectorBusinessPartnerReader;
import com.contactcore.connector.model.CrmConnectorCapability;
import com.contactcore.connector.model.CrmConnectorType;
import java.util.Set;

public interface CrmConnector {
    CrmConnectorType type();

    Set<CrmConnectorCapability> capabilities();

    CrmConnectorSessionProvider sessions();

    ConnectorBusinessPartnerReader businessPartners();
}

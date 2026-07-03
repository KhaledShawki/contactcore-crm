// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.port;

import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerFinancialProfile;
import com.contactcore.connector.port.ConnectorExecutionContext;
import java.util.Optional;

public interface ConnectorBusinessPartnerFinancialReader {
    Optional<ConnectorBusinessPartnerFinancialProfile> findFinancialProfile(ConnectorExecutionContext context, String businessPartnerExternalId);
}

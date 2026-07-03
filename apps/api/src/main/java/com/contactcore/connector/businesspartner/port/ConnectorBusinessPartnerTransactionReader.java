// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.port;

import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerTransactionSummary;
import com.contactcore.connector.port.ConnectorExecutionContext;
import java.util.Optional;

public interface ConnectorBusinessPartnerTransactionReader {
    Optional<ConnectorBusinessPartnerTransactionSummary> summarizeTransactions(ConnectorExecutionContext context, String businessPartnerExternalId);
}

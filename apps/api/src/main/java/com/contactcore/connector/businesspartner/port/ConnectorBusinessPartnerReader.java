// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.port;

import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerDetail;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerSummary;
import com.contactcore.connector.businesspartner.query.ConnectorBusinessPartnerSearchCriteria;
import com.contactcore.connector.port.ConnectorExecutionContext;
import com.contactcore.shared.api.PageResponse;
import java.util.Optional;

public interface ConnectorBusinessPartnerReader {
    PageResponse<ConnectorBusinessPartnerSummary> search(ConnectorExecutionContext context, ConnectorBusinessPartnerSearchCriteria criteria);

    Optional<ConnectorBusinessPartnerDetail> findByExternalId(ConnectorExecutionContext context, String externalId);
}

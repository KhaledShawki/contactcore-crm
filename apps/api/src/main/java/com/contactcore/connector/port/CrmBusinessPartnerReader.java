// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.port;

import com.contactcore.connector.model.CrmBusinessPartnerSearchCriteria;
import com.contactcore.connector.model.CrmBusinessPartnerView;
import com.contactcore.shared.api.PageResponse;
import java.util.Optional;

public interface CrmBusinessPartnerReader {
    PageResponse<CrmBusinessPartnerView> search(ConnectorExecutionContext context, CrmBusinessPartnerSearchCriteria criteria);

    Optional<CrmBusinessPartnerView> findByExternalId(ConnectorExecutionContext context, String externalId);
}

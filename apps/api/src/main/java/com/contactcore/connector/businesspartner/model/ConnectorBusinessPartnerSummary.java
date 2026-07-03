// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import com.contactcore.connector.model.CrmConnectorCapability;

public record ConnectorBusinessPartnerSummary(
        ConnectorBusinessPartnerIdentity identity,
        ConnectorBusinessPartnerType type,
        ConnectorBusinessPartnerStatus status,
        String primaryEmail,
        String primaryPhone,
        String website,
        String currency,
        BigDecimal balance,
        Instant sourceUpdatedAt,
        List<CrmConnectorCapability> capabilities
) {
    public ConnectorBusinessPartnerSummary {
        if (identity == null) {
            throw new IllegalArgumentException("identity must not be null.");
        }
        type = type == null ? ConnectorBusinessPartnerType.UNKNOWN : type;
        status = status == null ? new ConnectorBusinessPartnerStatus(true, false, true, "ACTIVE", null) : status;
        primaryEmail = blankToNull(primaryEmail);
        primaryPhone = blankToNull(primaryPhone);
        website = blankToNull(website);
        currency = blankToNull(currency);
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

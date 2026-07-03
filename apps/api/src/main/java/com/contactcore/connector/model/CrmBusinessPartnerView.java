// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.model;

import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerAddress;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerDetail;
import com.contactcore.connector.businesspartner.model.ConnectorBusinessPartnerSummary;
import java.math.BigDecimal;
import java.util.List;

public record CrmBusinessPartnerView(
        Long id,
        String externalId,
        String sourceSystem,
        Long connectorInstanceId,
        String connectorDisplayName,
        String kind,
        String statusCode,
        String statusName,
        String sourceCode,
        String code,
        String name,
        String primaryEmail,
        String primaryPhone,
        String website,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String countryCode,
        String notes,
        String currency,
        BigDecimal balance,
        boolean readOnly,
        List<CrmConnectorCapability> capabilities
) {
    public CrmBusinessPartnerView {
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
        readOnly = true;
    }

    public static CrmBusinessPartnerView fromConnectorBusinessPartnerSummary(ConnectorBusinessPartnerSummary businessPartner) {
        return new CrmBusinessPartnerView(
                null,
                businessPartner.identity().externalId(),
                businessPartner.identity().connectorType(),
                businessPartner.identity().connectorInstanceId(),
                businessPartner.identity().source() == null ? null : businessPartner.identity().source().connectorDisplayName(),
                businessPartner.type().name(),
                businessPartner.status().lifecycleStatus(),
                businessPartner.status().displayName(),
                businessPartner.identity().source() == null ? null : businessPartner.identity().source().displayReference(),
                businessPartner.identity().code(),
                businessPartner.identity().displayName(),
                businessPartner.primaryEmail(),
                businessPartner.primaryPhone(),
                businessPartner.website(),
                null,
                null,
                null,
                null,
                null,
                "Read-only CRM connector business partner.",
                businessPartner.currency(),
                businessPartner.balance(),
                true,
                businessPartner.capabilities()
        );
    }

    public static CrmBusinessPartnerView fromConnectorBusinessPartnerDetail(ConnectorBusinessPartnerDetail businessPartner) {
        ConnectorBusinessPartnerSummary summary = businessPartner.toSummary();
        ConnectorBusinessPartnerAddress primaryAddress = businessPartner.addresses().stream().findFirst().orElse(null);
        return new CrmBusinessPartnerView(
                null,
                businessPartner.identity().externalId(),
                businessPartner.identity().connectorType(),
                businessPartner.identity().connectorInstanceId(),
                businessPartner.identity().source() == null ? null : businessPartner.identity().source().connectorDisplayName(),
                businessPartner.type().name(),
                businessPartner.status().lifecycleStatus(),
                businessPartner.status().displayName(),
                businessPartner.identity().source() == null ? null : businessPartner.identity().source().displayReference(),
                businessPartner.identity().code(),
                businessPartner.identity().displayName(),
                summary.primaryEmail(),
                summary.primaryPhone(),
                summary.website(),
                primaryAddress == null ? null : primaryAddress.street(),
                primaryAddress == null ? null : primaryAddress.block(),
                primaryAddress == null ? null : primaryAddress.city(),
                primaryAddress == null ? null : primaryAddress.zipCode(),
                primaryAddress == null ? null : primaryAddress.countryCode(),
                "Read-only CRM connector business partner.",
                businessPartner.financialProfile().currency(),
                businessPartner.financialProfile().currentBalance(),
                true,
                summary.capabilities()
        );
    }
}

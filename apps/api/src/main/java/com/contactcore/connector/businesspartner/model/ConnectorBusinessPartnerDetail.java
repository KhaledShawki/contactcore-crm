// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

import com.contactcore.connector.model.CrmConnectorCapability;
import java.util.List;
import java.util.Map;

public record ConnectorBusinessPartnerDetail(
        ConnectorBusinessPartnerIdentity identity,
        ConnectorBusinessPartnerType type,
        ConnectorBusinessPartnerStatus status,
        ConnectorBusinessPartnerCommercialProfile commercialProfile,
        ConnectorBusinessPartnerFinancialProfile financialProfile,
        List<ConnectorBusinessPartnerAddress> addresses,
        List<ConnectorBusinessPartnerContactPerson> contactPersons,
        List<ConnectorBusinessPartnerContactPoint> contactPoints,
        ConnectorBusinessPartnerTransactionSummary transactionSummary,
        Map<String, Object> extensionFields,
        List<CrmConnectorCapability> capabilities
) {
    public ConnectorBusinessPartnerDetail {
        if (identity == null) {
            throw new IllegalArgumentException("identity must not be null.");
        }
        type = type == null ? ConnectorBusinessPartnerType.UNKNOWN : type;
        status = status == null ? new ConnectorBusinessPartnerStatus(true, false, true, "ACTIVE", null) : status;
        commercialProfile = commercialProfile == null ? ConnectorBusinessPartnerCommercialProfile.empty() : commercialProfile;
        financialProfile = financialProfile == null ? ConnectorBusinessPartnerFinancialProfile.empty() : financialProfile;
        addresses = addresses == null ? List.of() : List.copyOf(addresses);
        contactPersons = contactPersons == null ? List.of() : List.copyOf(contactPersons);
        contactPoints = contactPoints == null ? List.of() : List.copyOf(contactPoints);
        transactionSummary = transactionSummary == null ? ConnectorBusinessPartnerTransactionSummary.empty() : transactionSummary;
        extensionFields = extensionFields == null ? Map.of() : Map.copyOf(extensionFields);
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
    }

    public ConnectorBusinessPartnerSummary toSummary() {
        return new ConnectorBusinessPartnerSummary(
                identity,
                type,
                status,
                primaryEmail(),
                primaryPhone(),
                primaryWebsite(),
                financialProfile.currency(),
                financialProfile.currentBalance(),
                null,
                capabilities
        );
    }

    private String primaryEmail() {
        return contactPoints.stream()
                .filter(point -> point.type() == ConnectorContactPointType.EMAIL)
                .filter(ConnectorBusinessPartnerContactPoint::primary)
                .map(ConnectorBusinessPartnerContactPoint::value)
                .findFirst()
                .orElse(null);
    }

    private String primaryPhone() {
        return contactPoints.stream()
                .filter(point -> point.type() == ConnectorContactPointType.PHONE || point.type() == ConnectorContactPointType.MOBILE)
                .filter(ConnectorBusinessPartnerContactPoint::primary)
                .map(ConnectorBusinessPartnerContactPoint::value)
                .findFirst()
                .orElseGet(() -> contactPoints.stream()
                        .filter(point -> point.type() == ConnectorContactPointType.PHONE || point.type() == ConnectorContactPointType.MOBILE)
                        .map(ConnectorBusinessPartnerContactPoint::value)
                        .findFirst()
                        .orElse(null));
    }

    private String primaryWebsite() {
        return contactPoints.stream()
                .filter(point -> point.type() == ConnectorContactPointType.WEBSITE)
                .filter(ConnectorBusinessPartnerContactPoint::primary)
                .map(ConnectorBusinessPartnerContactPoint::value)
                .findFirst()
                .orElse(null);
    }
}

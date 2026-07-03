// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

public record ConnectorBusinessPartnerCommercialProfile(
        String salesEmployeeCode,
        String salesEmployeeName,
        String priceListCode,
        String priceListName,
        String territoryCode,
        String territoryName,
        String industryCode,
        String industryName,
        String groupCode,
        String groupName
) {
    public static ConnectorBusinessPartnerCommercialProfile empty() {
        return new ConnectorBusinessPartnerCommercialProfile(null, null, null, null, null, null, null, null, null, null);
    }
}

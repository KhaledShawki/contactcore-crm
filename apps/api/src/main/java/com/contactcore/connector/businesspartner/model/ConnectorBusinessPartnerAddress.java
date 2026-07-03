// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

public record ConnectorBusinessPartnerAddress(
        String externalAddressId,
        ConnectorAddressType type,
        String label,
        String street,
        String block,
        String zipCode,
        String city,
        String county,
        String state,
        String countryCode,
        boolean defaultBilling,
        boolean defaultShipping,
        ConnectorBusinessPartnerSourceReference source
) {
    public ConnectorBusinessPartnerAddress {
        type = type == null ? ConnectorAddressType.UNKNOWN : type;
        externalAddressId = blankToNull(externalAddressId);
        label = blankToNull(label);
        street = blankToNull(street);
        block = blankToNull(block);
        zipCode = blankToNull(zipCode);
        city = blankToNull(city);
        county = blankToNull(county);
        state = blankToNull(state);
        countryCode = blankToNull(countryCode);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

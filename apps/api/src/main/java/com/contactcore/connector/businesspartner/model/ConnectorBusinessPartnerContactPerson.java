// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

public record ConnectorBusinessPartnerContactPerson(
        String externalContactId,
        String firstName,
        String middleName,
        String lastName,
        String displayName,
        String title,
        String position,
        String email,
        String phone,
        String mobile,
        boolean primary,
        boolean active,
        ConnectorBusinessPartnerSourceReference source
) {
    public ConnectorBusinessPartnerContactPerson {
        externalContactId = blankToNull(externalContactId);
        firstName = blankToNull(firstName);
        middleName = blankToNull(middleName);
        lastName = blankToNull(lastName);
        displayName = blankToNull(displayName);
        title = blankToNull(title);
        position = blankToNull(position);
        email = blankToNull(email);
        phone = blankToNull(phone);
        mobile = blankToNull(mobile);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.application;

import com.contactcore.crm.api.BusinessPartnerWriteRequest;

record NormalizedBusinessPartnerInput(
        String kind,
        String statusCode,
        String code,
        String name,
        String primaryEmail,
        String primaryPhone,
        String website,
        String sourceCode,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String countryCode,
        String notes
) {}

final class BusinessPartnerInputNormalizer {
    private BusinessPartnerInputNormalizer() {}

    static NormalizedBusinessPartnerInput normalize(BusinessPartnerWriteRequest request) {
        return new NormalizedBusinessPartnerInput(
                requiredCode(request.kind()),
                requiredCode(request.statusCode()),
                requiredCode(request.code()),
                requiredText(request.name()),
                nullableLower(request.primaryEmail()),
                nullableText(request.primaryPhone()),
                nullableText(request.website()),
                nullableCode(request.sourceCode()),
                nullableText(request.addressLine1()),
                nullableText(request.addressLine2()),
                nullableText(request.city()),
                nullableText(request.postalCode()),
                nullableCode(request.countryCode()),
                nullableText(request.notes())
        );
    }

    static String requiredCode(String value) {
        return value.trim().toUpperCase();
    }

    private static String nullableCode(String value) {
        String text = nullableText(value);
        return text == null ? null : text.toUpperCase();
    }

    private static String nullableLower(String value) {
        String text = nullableText(value);
        return text == null ? null : text.toLowerCase();
    }

    private static String requiredText(String value) {
        return value.trim();
    }

    private static String nullableText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

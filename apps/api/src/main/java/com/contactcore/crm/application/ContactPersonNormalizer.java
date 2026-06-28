// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.application;

import com.contactcore.crm.api.ContactPersonWriteRequest;
import com.contactcore.shared.api.InvalidRequestException;

record NormalizedContactPersonInput(
        String firstName,
        String lastName,
        String roleTitle,
        String email,
        String phone,
        String mobile,
        String department,
        boolean primaryContact,
        String notes
) {}

final class ContactPersonNormalizer {
    private ContactPersonNormalizer() {}

    static NormalizedContactPersonInput normalize(ContactPersonWriteRequest request) {
        String email = nullableLower(request.email());
        String phone = nullableText(request.phone());
        String mobile = nullableText(request.mobile());
        if (email == null && phone == null && mobile == null) {
            throw new InvalidRequestException("A contact person needs at least one email, phone, or mobile number.");
        }
        return new NormalizedContactPersonInput(
                requiredText(request.firstName()),
                requiredText(request.lastName()),
                nullableText(request.roleTitle()),
                email,
                phone,
                mobile,
                nullableText(request.department()),
                request.primaryContact(),
                nullableText(request.notes())
        );
    }

    private static String requiredText(String value) {
        String text = nullableText(value);
        if (text == null) {
            throw new InvalidRequestException("Contact person first name and last name are required.");
        }
        return text;
    }

    private static String nullableText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String nullableLower(String value) {
        String text = nullableText(value);
        return text == null ? null : text.toLowerCase();
    }
}

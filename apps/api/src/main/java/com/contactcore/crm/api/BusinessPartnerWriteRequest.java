// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BusinessPartnerWriteRequest(
        @NotBlank @Size(max = 32) String kind,
        @NotBlank @Size(max = 32) String statusCode,
        @NotBlank @Size(max = 64) String code,
        @NotBlank @Size(max = 255) String name,
        @Email @Size(max = 255) String primaryEmail,
        @Size(max = 64) String primaryPhone,
        @Size(max = 255) String website,
        @Size(max = 64) String sourceCode,
        @Size(max = 255) String addressLine1,
        @Size(max = 255) String addressLine2,
        @Size(max = 128) String city,
        @Size(max = 64) String postalCode,
        @Size(max = 2) String countryCode,
        String notes
) {}

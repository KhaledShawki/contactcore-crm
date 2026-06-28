// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.marketing.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MarketingSourceWriteRequest(
        @NotBlank @Size(max = 64) String code,
        @NotBlank @Size(max = 120) String name,
        @Min(0) @Max(10_000) Integer sortOrder
) {}

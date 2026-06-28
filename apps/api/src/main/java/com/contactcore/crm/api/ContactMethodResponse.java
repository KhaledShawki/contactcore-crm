// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.api;

public record ContactMethodResponse(
        String typeCode,
        String label,
        String value,
        boolean primary
) {}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.api;

import java.util.List;

public record ConnectorInstanceResponse(
        Long id,
        String type,
        String displayName,
        String environment,
        List<String> capabilities,
        boolean canReadBusinessPartners
) {}

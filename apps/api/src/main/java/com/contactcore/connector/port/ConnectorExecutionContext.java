// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.port;

import com.contactcore.connector.domain.CrmConnectorInstance;

public record ConnectorExecutionContext(
        Long contactCoreUserId,
        CrmConnectorInstance instance,
        ConnectorAdapterSession session
) {}

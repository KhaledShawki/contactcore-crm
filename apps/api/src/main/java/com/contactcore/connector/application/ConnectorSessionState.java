// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.application;

import com.contactcore.connector.domain.CrmConnectorInstance;
import com.contactcore.connector.port.ConnectorAdapterSession;
import java.time.Instant;

public record ConnectorSessionState(
        Long userId,
        CrmConnectorInstance instance,
        ConnectorAdapterSession adapterSession,
        Instant connectedAt
) {
    public String externalUsername() {
        return adapterSession.externalUsername();
    }
}

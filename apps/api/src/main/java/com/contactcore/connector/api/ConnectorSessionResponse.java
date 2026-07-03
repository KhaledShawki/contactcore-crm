// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.api;

import java.time.Instant;

public record ConnectorSessionResponse(
        boolean connected,
        Long connectorInstanceId,
        String connectorType,
        String connectorDisplayName,
        String environment,
        String externalUsername,
        Instant connectedAt
) {
    public static ConnectorSessionResponse disconnected() {
        return new ConnectorSessionResponse(false, null, null, null, null, null, null);
    }

    public static ConnectorSessionResponse connected(Long connectorInstanceId,
                                                     String connectorType,
                                                     String connectorDisplayName,
                                                     String environment,
                                                     String externalUsername,
                                                     Instant connectedAt) {
        return new ConnectorSessionResponse(true, connectorInstanceId, connectorType, connectorDisplayName, environment, externalUsername, connectedAt);
    }
}

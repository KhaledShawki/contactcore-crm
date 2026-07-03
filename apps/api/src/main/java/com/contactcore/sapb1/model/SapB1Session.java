// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.model;

import com.contactcore.connector.port.ConnectorAdapterSession;
import java.time.Instant;

public record SapB1Session(
        String externalUsername,
        String b1Session,
        String routeId,
        Instant connectedAt
) implements ConnectorAdapterSession {
    public String cookieHeader() {
        StringBuilder builder = new StringBuilder("B1SESSION=").append(b1Session);
        if (routeId != null && !routeId.isBlank()) {
            builder.append("; ROUTEID=").append(routeId);
        }
        return builder.toString();
    }
}

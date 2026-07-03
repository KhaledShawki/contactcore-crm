// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.port;

import java.time.Instant;

public interface ConnectorAdapterSession {
    String externalUsername();

    Instant connectedAt();
}

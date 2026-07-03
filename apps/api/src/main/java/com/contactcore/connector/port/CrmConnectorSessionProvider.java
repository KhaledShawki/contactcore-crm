// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.port;

import com.contactcore.connector.domain.CrmConnectorInstance;

public interface CrmConnectorSessionProvider {
    ConnectorAdapterSession login(CrmConnectorInstance instance, String username, String password);

    default void logout(CrmConnectorInstance instance, ConnectorAdapterSession session) {
        // Connectors may override when their upstream supports explicit logout.
    }
}

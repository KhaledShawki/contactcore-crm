// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.application;

import com.contactcore.connector.model.CrmConnectorType;
import com.contactcore.connector.port.CrmConnector;
import com.contactcore.shared.api.InvalidRequestException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ConnectorRegistry {
    private final Map<CrmConnectorType, CrmConnector> connectorsByType;

    public ConnectorRegistry(List<CrmConnector> connectors) {
        Map<CrmConnectorType, CrmConnector> indexed = new EnumMap<>(CrmConnectorType.class);
        for (CrmConnector connector : connectors) {
            CrmConnector previous = indexed.putIfAbsent(connector.type(), connector);
            if (previous != null) {
                throw new IllegalStateException("Duplicate CRM connector registered: " + connector.type());
            }
        }
        this.connectorsByType = Map.copyOf(indexed);
    }

    public CrmConnector require(CrmConnectorType type) {
        CrmConnector connector = connectorsByType.get(type);
        if (connector == null) {
            throw new InvalidRequestException("CRM connector is not available: " + type);
        }
        return connector;
    }
}

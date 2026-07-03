// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1;

import com.contactcore.connector.domain.CrmConnectorInstance;
import com.contactcore.connector.port.ConnectorAdapterSession;
import com.contactcore.connector.port.CrmConnectorSessionProvider;
import com.contactcore.sapb1.client.SapB1ServiceLayerClient;
import com.contactcore.sapb1.model.SapB1ConnectorConfiguration;
import com.contactcore.sapb1.model.SapB1Session;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class SapB1SessionProvider implements CrmConnectorSessionProvider {
    private final ObjectMapper objectMapper;
    private final SapB1ServiceLayerClient client;

    public SapB1SessionProvider(ObjectMapper objectMapper, SapB1ServiceLayerClient client) {
        this.objectMapper = objectMapper;
        this.client = client;
    }

    @Override
    public ConnectorAdapterSession login(CrmConnectorInstance instance, String username, String password) {
        SapB1ConnectorConfiguration configuration = SapB1ConnectorConfiguration.from(instance, objectMapper);
        return client.login(configuration, username, password);
    }

    @Override
    public void logout(CrmConnectorInstance instance, ConnectorAdapterSession session) {
        if (session instanceof SapB1Session sapSession) {
            SapB1ConnectorConfiguration configuration = SapB1ConnectorConfiguration.from(instance, objectMapper);
            client.logout(configuration, sapSession);
        }
    }
}

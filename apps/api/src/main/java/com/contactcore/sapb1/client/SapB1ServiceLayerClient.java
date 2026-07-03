// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.client;

import com.contactcore.sapb1.model.SapB1ConnectorConfiguration;
import com.contactcore.sapb1.model.SapB1Session;

/**
 * Transport port for SAP Business One Service Layer calls.
 *
 * <p>This contract is intentionally resource-neutral. SAP entities such as
 * BusinessPartners, Items, Orders, or Invoices must be modeled in dedicated
 * resource gateways instead of being added here.</p>
 */
public interface SapB1ServiceLayerClient {
    SapB1Session login(SapB1ConnectorConfiguration configuration, String username, String password);

    void logout(SapB1ConnectorConfiguration configuration, SapB1Session session);

    <T> T get(SapB1ConnectorConfiguration configuration, SapB1Session session, String resourcePath, Class<T> responseType);

    <T> T post(SapB1ConnectorConfiguration configuration,
               SapB1Session session,
               String resourcePath,
               Object requestBody,
               Class<T> responseType);
}

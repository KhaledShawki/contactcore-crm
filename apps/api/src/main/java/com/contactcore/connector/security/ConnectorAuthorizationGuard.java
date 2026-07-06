// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.security;

import com.contactcore.iam.application.ContactCoreTenantContext;
import com.contactcore.iam.application.CurrentIamAuthorizationService;
import com.contactcore.shared.api.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class ConnectorAuthorizationGuard {
    private final CurrentIamAuthorizationService authorization;
    private final ContactCoreTenantContext tenantContext;

    public ConnectorAuthorizationGuard(CurrentIamAuthorizationService authorization, ContactCoreTenantContext tenantContext) {
        this.authorization = authorization;
        this.tenantContext = tenantContext;
    }

    public void requireReadInstances() {
        authorization.requireAllowed(ConnectorIamActions.READ, ConnectorIamResources.instances(tenantId()));
    }

    public void requireReadSession() {
        authorization.requireAllowed(ConnectorIamActions.READ, ConnectorIamResources.currentSession(tenantId()));
    }

    public void requireConnectSession(Long connectorInstanceId) {
        Long safeConnectorInstanceId = requireId(connectorInstanceId, "connectorInstanceId");
        authorization.requireAllowed(
                ConnectorIamActions.CONNECT_SESSION,
                ConnectorIamResources.instance(tenantId(), safeConnectorInstanceId),
                ConnectorAuthorizationContext.forSession(safeConnectorInstanceId, "connectSession").toRequestContext()
        );
    }

    public void requireDisconnectSession() {
        authorization.requireAllowed(
                ConnectorIamActions.DISCONNECT_SESSION,
                ConnectorIamResources.currentSession(tenantId()),
                ConnectorAuthorizationContext.forSession(null, "disconnectSession").toRequestContext()
        );
    }

    public void requireReadBusinessPartners(ConnectorAuthorizationContext context) {
        ConnectorAuthorizationContext safeContext = context == null ? ConnectorAuthorizationContext.empty() : context;
        authorization.requireAllowed(
                ConnectorIamActions.READ_BUSINESS_PARTNERS,
                ConnectorIamResources.businessPartners(tenantId()),
                safeContext.toRequestContext()
        );
    }

    public void requireReadBusinessPartner(String externalId) {
        String safeExternalId = requireText(externalId, "externalId");
        authorization.requireAllowed(
                ConnectorIamActions.READ_BUSINESS_PARTNERS,
                ConnectorIamResources.businessPartner(tenantId(), safeExternalId),
                ConnectorAuthorizationContext.forBusinessPartnerRead(safeExternalId).toRequestContext()
        );
    }

    private String tenantId() {
        return tenantContext.currentTenantId();
    }

    private static Long requireId(Long value, String fieldName) {
        if (value == null) {
            throw new InvalidRequestException(fieldName + " must not be null");
        }
        return value;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.application;

import com.contactcore.connector.api.ConnectorLoginRequest;
import com.contactcore.connector.api.ConnectorSessionResponse;
import com.contactcore.connector.domain.CrmConnectorInstance;
import com.contactcore.connector.model.CrmConnectorCapability;
import com.contactcore.connector.port.ConnectorAdapterSession;
import com.contactcore.connector.port.CrmConnector;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;

@Service
public class ConnectorSessionService {
    private final ConnectorInstanceService instances;
    private final ConnectorRegistry registry;
    private final ConnectorAuditService auditService;
    private final ConcurrentMap<Long, ConnectorSessionState> activeSessionsByUser = new ConcurrentHashMap<>();

    public ConnectorSessionService(ConnectorInstanceService instances, ConnectorRegistry registry, ConnectorAuditService auditService) {
        this.instances = instances;
        this.registry = registry;
        this.auditService = auditService;
    }

    public ConnectorSessionResponse login(Long userId, ConnectorLoginRequest request) {
        CrmConnectorInstance instance = instances.requireAccessibleInstance(
                userId,
                request.connectorInstanceId(),
                CrmConnectorCapability.READ_BUSINESS_PARTNERS
        );
        CrmConnector connector = registry.require(instance.getType());
        try {
            ConnectorAdapterSession adapterSession = connector.sessions().login(instance, request.username(), request.password());
            replaceActiveSession(userId, new ConnectorSessionState(userId, instance, adapterSession, Instant.now()));
            auditService.record(userId, instance.getId(), "CONNECTOR_LOGIN", "SUCCESS", "Connected as " + adapterSession.externalUsername());
            return status(userId);
        } catch (RuntimeException exception) {
            auditService.record(userId, instance.getId(), "CONNECTOR_LOGIN", "FAILURE", exception.getMessage());
            throw exception;
        }
    }

    public ConnectorSessionResponse status(Long userId) {
        ConnectorSessionState state = activeSessionsByUser.get(userId);
        if (state == null) {
            return ConnectorSessionResponse.disconnected();
        }
        return ConnectorSessionResponse.connected(
                state.instance().getId(),
                state.instance().getType().name(),
                state.instance().getDisplayName(),
                state.instance().getEnvironment().name(),
                state.externalUsername(),
                state.connectedAt()
        );
    }

    public Optional<ConnectorSessionState> activeSession(Long userId) {
        return Optional.ofNullable(activeSessionsByUser.get(userId));
    }

    public ConnectorSessionState requireActiveSession(Long userId, CrmConnectorCapability capability) {
        ConnectorSessionState state = activeSessionsByUser.get(userId);
        if (state == null) {
            throw new ConnectorSessionRequiredException("No active CRM connector session. Select a CRM connector and log in first.");
        }
        instances.requireAccessibleInstance(userId, state.instance().getId(), capability);
        registry.require(state.instance().getType()).capabilities().stream()
                .filter(supported -> supported == capability)
                .findFirst()
                .orElseThrow(() -> new ConnectorAccessDeniedException("The active CRM connector does not support capability: " + capability));
        return state;
    }

    public void disconnect(Long userId) {
        ConnectorSessionState removed = activeSessionsByUser.remove(userId);
        if (removed == null) {
            return;
        }
        CrmConnector connector = registry.require(removed.instance().getType());
        try {
            connector.sessions().logout(removed.instance(), removed.adapterSession());
            auditService.record(userId, removed.instance().getId(), "CONNECTOR_LOGOUT", "SUCCESS", "Disconnected.");
        } catch (RuntimeException exception) {
            auditService.record(userId, removed.instance().getId(), "CONNECTOR_LOGOUT", "FAILURE", exception.getMessage());
        }
    }

    public void clearExpiredOrInvalidSession(Long userId, String reason) {
        ConnectorSessionState removed = activeSessionsByUser.remove(userId);
        if (removed != null) {
            auditService.record(userId, removed.instance().getId(), "CONNECTOR_SESSION_CLEARED", "SUCCESS", reason);
        }
    }

    private void replaceActiveSession(Long userId, ConnectorSessionState nextSession) {
        ConnectorSessionState previous = activeSessionsByUser.put(userId, nextSession);
        if (previous != null) {
            try {
                CrmConnector previousConnector = registry.require(previous.instance().getType());
                previousConnector.sessions().logout(previous.instance(), previous.adapterSession());
            } catch (RuntimeException ignored) {
                // Replacing the active session must not fail because a previous upstream logout was unavailable.
            }
        }
    }
}

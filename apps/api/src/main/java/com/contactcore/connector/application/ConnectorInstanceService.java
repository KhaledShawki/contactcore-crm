// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.application;

import com.contactcore.connector.api.ConnectorInstanceResponse;
import com.contactcore.connector.domain.CrmConnectorInstance;
import com.contactcore.connector.domain.CrmConnectorUserAccess;
import com.contactcore.connector.domain.CrmConnectorUserAccessRepository;
import com.contactcore.connector.model.CrmConnectorCapability;
import com.contactcore.connector.port.CrmConnector;
import com.contactcore.shared.api.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConnectorInstanceService {
    private final CrmConnectorUserAccessRepository accessRepository;
    private final ConnectorRegistry registry;

    public ConnectorInstanceService(CrmConnectorUserAccessRepository accessRepository, ConnectorRegistry registry) {
        this.accessRepository = accessRepository;
        this.registry = registry;
    }

    @Transactional(readOnly = true)
    public List<ConnectorInstanceResponse> availableInstances(Long userId) {
        return accessRepository.findEnabledAccessForUser(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CrmConnectorInstance requireAccessibleInstance(Long userId, Long instanceId, CrmConnectorCapability capability) {
        CrmConnectorUserAccess access = accessRepository.findEnabledAccess(userId, instanceId)
                .orElseThrow(() -> new NotFoundException("CRM connector instance not found or not allowed: " + instanceId));
        if (capability == CrmConnectorCapability.READ_BUSINESS_PARTNERS && !access.canReadBusinessPartners()) {
            throw new ConnectorAccessDeniedException("You are not allowed to read business partners from this CRM connector.");
        }
        CrmConnector connector = registry.require(access.getConnectorInstance().getType());
        if (!connector.capabilities().contains(capability)) {
            throw new ConnectorAccessDeniedException("The selected CRM connector does not support capability: " + capability);
        }
        return access.getConnectorInstance();
    }

    private ConnectorInstanceResponse toResponse(CrmConnectorUserAccess access) {
        CrmConnectorInstance instance = access.getConnectorInstance();
        CrmConnector connector = registry.require(instance.getType());
        return new ConnectorInstanceResponse(
                instance.getId(),
                instance.getType().name(),
                instance.getDisplayName(),
                instance.getEnvironment().name(),
                connector.capabilities().stream().map(Enum::name).sorted().toList(),
                access.canReadBusinessPartners()
        );
    }
}

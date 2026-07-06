// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.config;

import com.contactcore.connector.domain.CrmConnectorInstance;
import com.contactcore.connector.domain.CrmConnectorInstanceRepository;
import com.contactcore.connector.domain.CrmConnectorUserAccess;
import com.contactcore.connector.domain.CrmConnectorUserAccessRepository;
import com.contactcore.connector.model.CrmConnectorType;
import com.contactcore.security.domain.AppUser;
import com.contactcore.security.domain.AppUserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(100)
public class SapB1ConnectorBootstrap implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(SapB1ConnectorBootstrap.class);
    private static final int SORT_ORDER = 100;

    private final SapB1Properties properties;
    private final CrmConnectorInstanceRepository connectorInstances;
    private final CrmConnectorUserAccessRepository connectorUserAccess;
    private final AppUserRepository users;
    private final ObjectMapper objectMapper;

    public SapB1ConnectorBootstrap(SapB1Properties properties,
                                   CrmConnectorInstanceRepository connectorInstances,
                                   CrmConnectorUserAccessRepository connectorUserAccess,
                                   AppUserRepository users,
                                   ObjectMapper objectMapper) {
        this.properties = properties;
        this.connectorInstances = connectorInstances;
        this.connectorUserAccess = connectorUserAccess;
        this.users = users;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!properties.enabled()) {
            return;
        }
        if (!properties.hasConnectionConfig()) {
            throw new IllegalStateException(
                    "SAP B1 connector is enabled but CONTACTCORE_SAP_B1_SERVICE_LAYER_BASE_URL and CONTACTCORE_SAP_B1_COMPANY_DB are not fully configured."
            );
        }

        CrmConnectorInstance instance = connectorInstances
                .findFirstByTypeAndArchivedAtIsNull(CrmConnectorType.SAP_B1)
                .orElseGet(this::newSapB1Instance);

        instance.updateBootstrapConfig(
                properties.normalizedDisplayName(),
                properties.normalizedEnvironment(),
                configJson(),
                true,
                SORT_ORDER
        );

        CrmConnectorInstance savedInstance = connectorInstances.save(instance);
        grantConfiguredUsers(savedInstance);
    }

    private CrmConnectorInstance newSapB1Instance() {
        return new CrmConnectorInstance(
                CrmConnectorType.SAP_B1,
                properties.normalizedDisplayName(),
                properties.normalizedEnvironment(),
                configJson()
        );
    }

    private String configJson() {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "serviceLayerBaseUrl", properties.normalizedServiceLayerBaseUrl(),
                    "companyDb", properties.normalizedCompanyDb(),
                    "timeoutMs", properties.timeoutMs()
            ));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize SAP B1 connector configuration.", exception);
        }
    }

    private void grantConfiguredUsers(CrmConnectorInstance instance) {
        for (String username : properties.normalizedGrantUsernames()) {
            users.findActiveByLogin(username)
                    .ifPresentOrElse(
                            user -> grantUser(instance, user),
                            () -> log.warn("SAP B1 connector access was not granted because user '{}' was not found.", username)
                    );
        }
    }

    private void grantUser(CrmConnectorInstance instance, AppUser user) {
        CrmConnectorUserAccess access = connectorUserAccess
                .findActiveAccess(user.getId(), instance.getId())
                .orElseGet(() -> new CrmConnectorUserAccess(user, instance, true));

        access.enableReadBusinessPartners();
        connectorUserAccess.save(access);
    }
}

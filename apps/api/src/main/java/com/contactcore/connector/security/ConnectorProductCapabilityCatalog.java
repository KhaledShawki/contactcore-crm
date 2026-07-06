// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.security;

import com.contactcore.iam.application.ProductCapabilityCatalog;
import com.contactcore.iam.evaluation.ProductCapabilityRule;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConnectorProductCapabilityCatalog implements ProductCapabilityCatalog {
    @Override
    public String service() {
        return ConnectorIamActions.SERVICE;
    }

    @Override
    public List<ProductCapabilityRule> rulesForTenant(String tenantId) {
        return List.of(
                new ProductCapabilityRule(ConnectorIamActions.READ, ConnectorIamResources.instances(tenantId)),
                new ProductCapabilityRule(ConnectorIamActions.READ, ConnectorIamResources.sessions(tenantId)),
                new ProductCapabilityRule(ConnectorIamActions.CONNECT_SESSION, ConnectorIamResources.instances(tenantId)),
                new ProductCapabilityRule(ConnectorIamActions.CONNECT_SESSION, ConnectorIamResources.sessions(tenantId)),
                new ProductCapabilityRule(ConnectorIamActions.DISCONNECT_SESSION, ConnectorIamResources.sessions(tenantId)),
                new ProductCapabilityRule(ConnectorIamActions.READ_BUSINESS_PARTNERS, ConnectorIamResources.businessPartners(tenantId)),
                new ProductCapabilityRule(ConnectorIamActions.CONFIGURE, ConnectorIamResources.instances(tenantId)),
                new ProductCapabilityRule(ConnectorIamActions.START_SYNC, ConnectorIamResources.instances(tenantId))
        );
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.domain;

import com.contactcore.connector.model.CrmConnectorEnvironment;
import com.contactcore.connector.model.CrmConnectorType;
import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "crm_connector_instance")
public class CrmConnectorInstance extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CrmConnectorType type;

    @Column(name = "display_name", nullable = false, length = 160)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CrmConnectorEnvironment environment = CrmConnectorEnvironment.TEST;

    @Column(name = "config_json", nullable = false, columnDefinition = "TEXT")
    private String configJson;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 100;

    protected CrmConnectorInstance() {}

    public CrmConnectorInstance(CrmConnectorType type, String displayName, CrmConnectorEnvironment environment, String configJson) {
        this.type = type;
        this.displayName = displayName;
        this.environment = environment;
        this.configJson = configJson;
    }

    public CrmConnectorType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public CrmConnectorEnvironment getEnvironment() {
        return environment;
    }

    public String getConfigJson() {
        return configJson;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}

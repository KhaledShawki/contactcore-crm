// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.domain;

import com.contactcore.shared.domain.BaseEntity;
import com.contactcore.security.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "crm_connector_user_access")
public class CrmConnectorUserAccess extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "connector_instance_id", nullable = false)
    private CrmConnectorInstance connectorInstance;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "can_read_business_partners", nullable = false)
    private boolean canReadBusinessPartners = true;

    protected CrmConnectorUserAccess() {}

    public AppUser getUser() {
        return user;
    }

    public CrmConnectorInstance getConnectorInstance() {
        return connectorInstance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean canReadBusinessPartners() {
        return canReadBusinessPartners;
    }
}

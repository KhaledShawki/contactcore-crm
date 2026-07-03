// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "crm_connector_audit_event")
public class CrmConnectorAuditEvent extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "connector_instance_id")
    private Long connectorInstanceId;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(nullable = false, length = 40)
    private String outcome;

    @Column(columnDefinition = "TEXT")
    private String details;

    protected CrmConnectorAuditEvent() {}

    public CrmConnectorAuditEvent(Long userId, Long connectorInstanceId, String action, String outcome, String details) {
        this.userId = userId;
        this.connectorInstanceId = connectorInstanceId;
        this.action = action;
        this.outcome = outcome;
        this.details = details;
    }
}

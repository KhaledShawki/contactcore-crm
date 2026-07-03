// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.application;

import com.contactcore.connector.domain.CrmConnectorAuditEvent;
import com.contactcore.connector.domain.CrmConnectorAuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConnectorAuditService {
    private final CrmConnectorAuditEventRepository events;

    public ConnectorAuditService(CrmConnectorAuditEventRepository events) {
        this.events = events;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(Long userId, Long connectorInstanceId, String action, String outcome, String details) {
        events.save(new CrmConnectorAuditEvent(userId, connectorInstanceId, action, outcome, details));
    }
}

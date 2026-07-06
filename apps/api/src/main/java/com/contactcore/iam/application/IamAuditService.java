// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamAuditEvent;
import com.contactcore.iam.domain.IamAuditEventRepository;
import com.contactcore.iam.domain.IamPrincipalRef;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IamAuditService {
    private final IamAuditEventRepository auditEvents;
    private final ContactCoreTenantContext tenantContext;

    public IamAuditService(IamAuditEventRepository auditEvents, ContactCoreTenantContext tenantContext) {
        this.auditEvents = auditEvents;
        this.tenantContext = tenantContext;
    }

    @Transactional
    public void record(IamPrincipalRef actor, String action, String targetType, String targetId, String outcome, String message) {
        auditEvents.save(new IamAuditEvent(
                tenantContext.currentTenantId(),
                actor,
                action,
                targetType,
                targetId,
                outcome,
                message
        ));
    }
}

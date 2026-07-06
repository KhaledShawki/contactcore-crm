// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "iam_audit_event")
public class IamAuditEvent extends BaseEntity {
    @Column(name = "tenant_id", nullable = false, length = 128)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_principal_type", length = 32)
    private IamPrincipalType actorPrincipalType;

    @Column(name = "actor_principal_id", length = 128)
    private String actorPrincipalId;

    @Column(nullable = false, length = 128)
    private String action;

    @Column(name = "target_type", nullable = false, length = 80)
    private String targetType;

    @Column(name = "target_id", length = 128)
    private String targetId;

    @Column(nullable = false, length = 32)
    private String outcome;

    @Column(columnDefinition = "text")
    private String message;

    protected IamAuditEvent() {}

    public IamAuditEvent(String tenantId, IamPrincipalRef actor, String action, String targetType, String targetId,
                         String outcome, String message) {
        this.tenantId = require(tenantId, "tenantId");
        setActor(actor);
        this.action = require(action, "action");
        this.targetType = require(targetType, "targetType");
        this.targetId = normalizeNullable(targetId);
        this.outcome = require(outcome, "outcome");
        this.message = normalizeNullable(message);
    }

    public String getTenantId() {
        return tenantId;
    }

    public IamPrincipalType getActorPrincipalType() {
        return actorPrincipalType;
    }

    public String getActorPrincipalId() {
        return actorPrincipalId;
    }

    public String getAction() {
        return action;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getMessage() {
        return message;
    }

    private void setActor(IamPrincipalRef actor) {
        if (actor == null) {
            return;
        }
        this.actorPrincipalType = actor.type();
        this.actorPrincipalId = actor.id();
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

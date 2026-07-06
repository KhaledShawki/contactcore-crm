// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "iam_permission_boundary")
public class IamPermissionBoundary extends BaseEntity {
    @Column(name = "tenant_id", nullable = false, length = 128)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "principal_type", nullable = false, length = 32)
    private IamPrincipalType principalType;

    @Column(name = "principal_id", nullable = false, length = 128)
    private String principalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private IamManagedPolicy policy;

    protected IamPermissionBoundary() {}

    public IamPermissionBoundary(String tenantId, IamPrincipalRef principal, IamManagedPolicy policy) {
        this.tenantId = require(tenantId, "tenantId");
        Objects.requireNonNull(principal, "principal must not be null");
        this.principalType = principal.type();
        this.principalId = principal.id();
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
    }

    public String getTenantId() {
        return tenantId;
    }

    public IamPrincipalType getPrincipalType() {
        return principalType;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public IamManagedPolicy getPolicy() {
        return policy;
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}

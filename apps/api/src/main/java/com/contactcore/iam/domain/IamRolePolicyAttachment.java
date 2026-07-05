// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import com.contactcore.security.domain.SecurityRole;
import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "iam_role_policy_attachment")
public class IamRolePolicyAttachment extends BaseEntity {
    @Column(name = "tenant_id", nullable = false, length = 128)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private SecurityRole role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private IamManagedPolicy policy;

    protected IamRolePolicyAttachment() {}

    public IamRolePolicyAttachment(String tenantId, SecurityRole role, IamManagedPolicy policy) {
        this.tenantId = require(tenantId, "tenantId");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
    }

    public String getTenantId() {
        return tenantId;
    }

    public SecurityRole getRole() {
        return role;
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

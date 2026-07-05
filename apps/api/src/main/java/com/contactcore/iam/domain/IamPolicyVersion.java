// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "iam_policy_version")
public class IamPolicyVersion extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private IamManagedPolicy policy;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Column(name = "document_json", nullable = false, columnDefinition = "text")
    private String documentJson;

    @Column(name = "default_version", nullable = false)
    private boolean defaultVersion;

    @Column(name = "created_by_principal_type", length = 32)
    private String createdByPrincipalType;

    @Column(name = "created_by_principal_id", length = 128)
    private String createdByPrincipalId;

    protected IamPolicyVersion() {}

    public IamPolicyVersion(IamManagedPolicy policy, int versionNumber, String documentJson, IamPrincipalRef createdBy) {
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
        if (versionNumber <= 0) {
            throw new IllegalArgumentException("versionNumber must be positive");
        }
        this.versionNumber = versionNumber;
        this.documentJson = require(documentJson, "documentJson");
        setCreatedBy(createdBy);
    }

    public IamManagedPolicy getPolicy() {
        return policy;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public String getDocumentJson() {
        return documentJson;
    }

    public boolean isDefaultVersion() {
        return defaultVersion;
    }

    public String getCreatedByPrincipalType() {
        return createdByPrincipalType;
    }

    public String getCreatedByPrincipalId() {
        return createdByPrincipalId;
    }

    public void markDefault() {
        this.defaultVersion = true;
    }

    public void clearDefault() {
        this.defaultVersion = false;
    }

    private void setCreatedBy(IamPrincipalRef createdBy) {
        if (createdBy == null) {
            return;
        }
        this.createdByPrincipalType = createdBy.type().name();
        this.createdByPrincipalId = createdBy.id();
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}

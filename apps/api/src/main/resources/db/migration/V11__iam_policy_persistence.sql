-- Copyright (c) Khaled Shawki. All rights reserved.

CREATE TABLE iam_managed_policy (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    code VARCHAR(120) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    description TEXT,
    system_managed BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE iam_policy_version (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    policy_id BIGINT NOT NULL REFERENCES iam_managed_policy(id) ON DELETE RESTRICT,
    version_number INTEGER NOT NULL CHECK (version_number > 0),
    document_json TEXT NOT NULL,
    default_version BOOLEAN NOT NULL DEFAULT FALSE,
    created_by_principal_type VARCHAR(32),
    created_by_principal_id VARCHAR(128),
    CONSTRAINT uq_iam_policy_version_number UNIQUE (policy_id, version_number)
);

CREATE UNIQUE INDEX uq_iam_policy_default_version
    ON iam_policy_version(policy_id)
    WHERE default_version = TRUE AND archived_at IS NULL;

CREATE INDEX idx_iam_policy_version_policy_default
    ON iam_policy_version(policy_id, default_version)
    WHERE archived_at IS NULL;

CREATE TABLE iam_role_policy_attachment (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    tenant_id VARCHAR(128) NOT NULL,
    role_id BIGINT NOT NULL REFERENCES security_role(id) ON DELETE CASCADE,
    policy_id BIGINT NOT NULL REFERENCES iam_managed_policy(id) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX uq_iam_role_policy_attachment_active
    ON iam_role_policy_attachment(tenant_id, role_id, policy_id)
    WHERE archived_at IS NULL;

CREATE INDEX idx_iam_role_policy_attachment_role
    ON iam_role_policy_attachment(role_id)
    WHERE archived_at IS NULL;

CREATE INDEX idx_iam_role_policy_attachment_policy
    ON iam_role_policy_attachment(policy_id)
    WHERE archived_at IS NULL;

CREATE TABLE iam_principal_policy_attachment (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    tenant_id VARCHAR(128) NOT NULL,
    principal_type VARCHAR(32) NOT NULL,
    principal_id VARCHAR(128) NOT NULL,
    policy_id BIGINT NOT NULL REFERENCES iam_managed_policy(id) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX uq_iam_principal_policy_attachment_active
    ON iam_principal_policy_attachment(tenant_id, principal_type, principal_id, policy_id)
    WHERE archived_at IS NULL;

CREATE INDEX idx_iam_principal_policy_attachment_principal
    ON iam_principal_policy_attachment(tenant_id, principal_type, principal_id)
    WHERE archived_at IS NULL;

CREATE TABLE iam_permission_boundary (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    tenant_id VARCHAR(128) NOT NULL,
    principal_type VARCHAR(32) NOT NULL,
    principal_id VARCHAR(128) NOT NULL,
    policy_id BIGINT NOT NULL REFERENCES iam_managed_policy(id) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX uq_iam_permission_boundary_active
    ON iam_permission_boundary(tenant_id, principal_type, principal_id)
    WHERE archived_at IS NULL;

CREATE TABLE iam_audit_event (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    tenant_id VARCHAR(128) NOT NULL,
    actor_principal_type VARCHAR(32),
    actor_principal_id VARCHAR(128),
    action VARCHAR(128) NOT NULL,
    target_type VARCHAR(80) NOT NULL,
    target_id VARCHAR(128),
    outcome VARCHAR(32) NOT NULL,
    message TEXT
);

CREATE INDEX idx_iam_audit_event_tenant_created
    ON iam_audit_event(tenant_id, created_at DESC)
    WHERE archived_at IS NULL;

CREATE INDEX idx_iam_audit_event_target
    ON iam_audit_event(tenant_id, target_type, target_id)
    WHERE archived_at IS NULL;

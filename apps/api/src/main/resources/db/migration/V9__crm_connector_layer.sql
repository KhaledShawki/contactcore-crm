-- Copyright (c) Khaled Shawki. All rights reserved.

CREATE TABLE crm_connector_instance (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    type VARCHAR(40) NOT NULL,
    display_name VARCHAR(160) NOT NULL,
    environment VARCHAR(20) NOT NULL DEFAULT 'TEST',
    config_json TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INTEGER NOT NULL DEFAULT 100,
    CONSTRAINT ck_crm_connector_type CHECK (type IN ('SAP_B1')),
    CONSTRAINT ck_crm_connector_environment CHECK (environment IN ('DEV', 'TEST', 'PROD'))
);

CREATE INDEX idx_crm_connector_instance_enabled
    ON crm_connector_instance(type, enabled, sort_order)
    WHERE archived_at IS NULL;

CREATE TABLE crm_connector_user_access (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    connector_instance_id BIGINT NOT NULL REFERENCES crm_connector_instance(id) ON DELETE CASCADE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    can_read_business_partners BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX uq_crm_connector_user_access_active
    ON crm_connector_user_access(user_id, connector_instance_id)
    WHERE archived_at IS NULL;

CREATE INDEX idx_crm_connector_user_access_user
    ON crm_connector_user_access(user_id, enabled)
    WHERE archived_at IS NULL;

CREATE TABLE crm_connector_audit_event (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    connector_instance_id BIGINT REFERENCES crm_connector_instance(id) ON DELETE SET NULL,
    action VARCHAR(80) NOT NULL,
    outcome VARCHAR(40) NOT NULL,
    details TEXT
);

CREATE INDEX idx_crm_connector_audit_event_user
    ON crm_connector_audit_event(user_id, created_at DESC)
    WHERE archived_at IS NULL;

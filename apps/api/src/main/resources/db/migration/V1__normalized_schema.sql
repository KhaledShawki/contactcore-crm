-- Copyright (c) Khaled Shawki. All rights reserved.

CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    username VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMPTZ
);

CREATE TABLE security_role (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL
);

CREATE TABLE app_user_role (
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES security_role(id) ON DELETE RESTRICT,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE stored_file (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    object_key VARCHAR(500) NOT NULL UNIQUE,
    original_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(128),
    size_bytes BIGINT NOT NULL CHECK (size_bytes >= 0)
);

CREATE TABLE user_profile (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    user_id BIGINT NOT NULL UNIQUE REFERENCES app_user(id) ON DELETE CASCADE,
    display_name VARCHAR(255) NOT NULL,
    phone VARCHAR(64),
    job_title VARCHAR(128),
    bio TEXT,
    locale VARCHAR(32) NOT NULL DEFAULT 'en',
    timezone VARCHAR(64) NOT NULL DEFAULT 'Europe/Berlin',
    profile_image_file_id BIGINT REFERENCES stored_file(id) ON DELETE SET NULL
);

CREATE TABLE business_partner_kind (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL
);

CREATE TABLE business_partner_status (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL
);

CREATE TABLE lead_source (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL
);

CREATE TABLE business_partner (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    kind_id BIGINT NOT NULL REFERENCES business_partner_kind(id) ON DELETE RESTRICT,
    status_id BIGINT NOT NULL REFERENCES business_partner_status(id) ON DELETE RESTRICT,
    lead_source_id BIGINT REFERENCES lead_source(id) ON DELETE SET NULL,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    notes TEXT
);

CREATE UNIQUE INDEX uq_business_partner_code_active ON business_partner(upper(code)) WHERE archived_at IS NULL;
CREATE INDEX idx_business_partner_kind ON business_partner(kind_id) WHERE archived_at IS NULL;
CREATE INDEX idx_business_partner_status ON business_partner(status_id) WHERE archived_at IS NULL;
CREATE INDEX idx_business_partner_name ON business_partner(lower(name)) WHERE archived_at IS NULL;

CREATE TABLE contact_method_type (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL
);

CREATE TABLE business_partner_contact_method (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    business_partner_id BIGINT NOT NULL REFERENCES business_partner(id) ON DELETE CASCADE,
    contact_method_type_id BIGINT NOT NULL REFERENCES contact_method_type(id) ON DELETE RESTRICT,
    label VARCHAR(80),
    value VARCHAR(255) NOT NULL,
    primary_contact BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX uq_bp_primary_contact_type
    ON business_partner_contact_method(business_partner_id, contact_method_type_id)
    WHERE primary_contact = TRUE AND archived_at IS NULL;
CREATE INDEX idx_bp_contact_value ON business_partner_contact_method(lower(value)) WHERE archived_at IS NULL;

CREATE TABLE address (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    line1 VARCHAR(255),
    line2 VARCHAR(255),
    city VARCHAR(128),
    postal_code VARCHAR(64),
    country_code VARCHAR(2)
);

CREATE TABLE business_partner_address (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    business_partner_id BIGINT NOT NULL REFERENCES business_partner(id) ON DELETE CASCADE,
    address_id BIGINT NOT NULL REFERENCES address(id) ON DELETE RESTRICT,
    address_type VARCHAR(32) NOT NULL DEFAULT 'PRIMARY',
    primary_address BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX uq_bp_primary_address
    ON business_partner_address(business_partner_id)
    WHERE primary_address = TRUE AND archived_at IS NULL;

CREATE TABLE document_type (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL
);

CREATE TABLE business_partner_document (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    business_partner_id BIGINT NOT NULL REFERENCES business_partner(id) ON DELETE CASCADE,
    stored_file_id BIGINT NOT NULL REFERENCES stored_file(id) ON DELETE RESTRICT,
    document_type_id BIGINT NOT NULL REFERENCES document_type(id) ON DELETE RESTRICT
);

CREATE INDEX idx_bp_document_partner ON business_partner_document(business_partner_id) WHERE archived_at IS NULL;

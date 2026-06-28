-- Copyright (c) Khaled Shawki. All rights reserved.

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE user_ui_settings (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    user_id BIGINT NOT NULL UNIQUE REFERENCES app_user(id) ON DELETE CASCADE,
    theme VARCHAR(32) NOT NULL DEFAULT 'ocean',
    text_size VARCHAR(32) NOT NULL DEFAULT 'comfortable',
    density VARCHAR(32) NOT NULL DEFAULT 'comfortable',
    sidebar_mode VARCHAR(32) NOT NULL DEFAULT 'expanded',
    reduce_motion BOOLEAN NOT NULL DEFAULT FALSE,
    high_contrast BOOLEAN NOT NULL DEFAULT FALSE,
    default_landing_page VARCHAR(120) NOT NULL DEFAULT '/dashboard',
    CONSTRAINT ck_user_ui_settings_theme CHECK (theme IN ('light', 'dark', 'ocean', 'graphite')),
    CONSTRAINT ck_user_ui_settings_text_size CHECK (text_size IN ('compact', 'comfortable', 'large')),
    CONSTRAINT ck_user_ui_settings_density CHECK (density IN ('compact', 'comfortable', 'spacious')),
    CONSTRAINT ck_user_ui_settings_sidebar CHECK (sidebar_mode IN ('expanded', 'compact')),
    CONSTRAINT ck_user_ui_settings_landing CHECK (default_landing_page IN ('/dashboard', '/customers', '/leads', '/suppliers', '/reports'))
);

CREATE TABLE business_partner_contact_person (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    business_partner_id BIGINT NOT NULL REFERENCES business_partner(id) ON DELETE CASCADE,
    first_name VARCHAR(120) NOT NULL,
    last_name VARCHAR(120) NOT NULL,
    role_title VARCHAR(160),
    email VARCHAR(255),
    phone VARCHAR(64),
    mobile VARCHAR(64),
    department VARCHAR(120),
    primary_contact BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    CONSTRAINT ck_contact_person_has_channel CHECK (email IS NOT NULL OR phone IS NOT NULL OR mobile IS NOT NULL)
);

CREATE UNIQUE INDEX uq_bp_contact_person_primary
    ON business_partner_contact_person(business_partner_id)
    WHERE primary_contact = TRUE AND archived_at IS NULL;

CREATE INDEX idx_bp_contact_person_partner_active
    ON business_partner_contact_person(business_partner_id, primary_contact DESC, last_name, first_name)
    WHERE archived_at IS NULL;

CREATE INDEX idx_bp_contact_person_name_trgm
    ON business_partner_contact_person USING gin (lower(first_name || ' ' || last_name) gin_trgm_ops)
    WHERE archived_at IS NULL;

CREATE INDEX idx_bp_contact_person_email_trgm
    ON business_partner_contact_person USING gin (lower(email) gin_trgm_ops)
    WHERE archived_at IS NULL AND email IS NOT NULL;

CREATE INDEX idx_bp_contact_person_phone_trgm
    ON business_partner_contact_person USING gin (lower(coalesce(phone, '') || ' ' || coalesce(mobile, '')) gin_trgm_ops)
    WHERE archived_at IS NULL;

CREATE INDEX idx_business_partner_code_trgm
    ON business_partner USING gin (lower(code) gin_trgm_ops)
    WHERE archived_at IS NULL;

CREATE INDEX idx_business_partner_name_trgm
    ON business_partner USING gin (lower(name) gin_trgm_ops)
    WHERE archived_at IS NULL;

CREATE INDEX idx_bp_contact_method_value_trgm
    ON business_partner_contact_method USING gin (lower(value) gin_trgm_ops)
    WHERE archived_at IS NULL;

CREATE INDEX idx_business_partner_sort_updated
    ON business_partner(updated_at DESC, id DESC)
    WHERE archived_at IS NULL;

CREATE INDEX idx_business_partner_sort_created
    ON business_partner(created_at DESC, id DESC)
    WHERE archived_at IS NULL;

CREATE INDEX idx_business_partner_sort_name
    ON business_partner(lower(name), id)
    WHERE archived_at IS NULL;

CREATE INDEX idx_lead_source_name_trgm
    ON lead_source USING gin (lower(name) gin_trgm_ops)
    WHERE archived_at IS NULL;

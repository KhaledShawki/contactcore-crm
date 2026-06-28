-- Copyright (c) Khaled Shawki. All rights reserved.

CREATE INDEX IF NOT EXISTS idx_business_partner_kind_updated_active
    ON business_partner(kind_id, updated_at DESC, id DESC)
    WHERE archived_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_business_partner_kind_created_active
    ON business_partner(kind_id, created_at DESC, id DESC)
    WHERE archived_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_business_partner_kind_code_active
    ON business_partner(kind_id, lower(code), id)
    WHERE archived_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_business_partner_kind_name_active
    ON business_partner(kind_id, lower(name), id)
    WHERE archived_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_business_partner_status_name_active
    ON business_partner(status_id, kind_id, lower(name), id)
    WHERE archived_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_bp_contact_method_partner_type_primary_active
    ON business_partner_contact_method(business_partner_id, contact_method_type_id, primary_contact, id)
    WHERE archived_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_bp_address_partner_primary_active
    ON business_partner_address(business_partner_id, primary_address, id)
    WHERE archived_at IS NULL;

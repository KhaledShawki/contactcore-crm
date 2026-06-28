-- Copyright (c) Khaled Shawki. All rights reserved.

ALTER TABLE lead_source
    ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 100;

UPDATE lead_source SET sort_order = 10 WHERE code = 'WEBSITE';
UPDATE lead_source SET sort_order = 20 WHERE code = 'REFERRAL';
UPDATE lead_source SET sort_order = 30 WHERE code = 'EVENT';
UPDATE lead_source SET sort_order = 40 WHERE code = 'DIRECT';

INSERT INTO lead_source (code, name, sort_order) VALUES
    ('LINKEDIN', 'LinkedIn', 50),
    ('GOOGLE_ADS', 'Google Ads', 60),
    ('EMAIL_CAMPAIGN', 'Email Campaign', 70),
    ('PARTNER', 'Partner Network', 80)
ON CONFLICT (code) DO NOTHING;

CREATE INDEX IF NOT EXISTS idx_business_partner_created_at_active ON business_partner(created_at) WHERE archived_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_business_partner_lead_source_active ON business_partner(lead_source_id) WHERE archived_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_lead_source_sort_active ON lead_source(sort_order, name) WHERE archived_at IS NULL;

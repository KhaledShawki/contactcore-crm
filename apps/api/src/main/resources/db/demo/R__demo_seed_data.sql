-- Copyright (c) Khaled Shawki. All rights reserved.

INSERT INTO lead_source (code, name, sort_order) VALUES
    ('ORGANIC_SEARCH', 'Organic Search', 90),
    ('TRADE_FAIR', 'Trade Fair', 100),
    ('WEBINAR', 'Webinar', 110),
    ('COLD_OUTREACH', 'Cold Outreach', 120),
    ('EXISTING_CUSTOMER', 'Existing Customer', 130)
ON CONFLICT (code) DO NOTHING;

WITH seed AS (
    SELECT number,
           CASE WHEN number <= 42 THEN 'CUSTOMER'
                WHEN number <= 78 THEN 'LEAD'
                ELSE 'SUPPLIER' END AS kind_code,
           CASE WHEN number <= 42 THEN 'CUS-' || lpad(number::text, 4, '0')
                WHEN number <= 78 THEN 'LED-' || lpad((number - 42)::text, 4, '0')
                ELSE 'SUP-' || lpad((number - 78)::text, 4, '0') END AS partner_code,
           CASE WHEN number <= 42 THEN 'Customer '
                WHEN number <= 78 THEN 'Lead '
                ELSE 'Supplier ' END || lpad(number::text, 3, '0') AS partner_name,
           CASE WHEN number % 11 = 0 THEN 'INACTIVE'
                WHEN number > 42 AND number <= 78 AND number % 3 = 0 THEN 'QUALIFIED'
                WHEN number > 42 AND number <= 78 THEN 'NEW'
                ELSE 'ACTIVE' END AS status_code,
           CASE number % 9
                WHEN 0 THEN 'WEBSITE'
                WHEN 1 THEN 'LINKEDIN'
                WHEN 2 THEN 'REFERRAL'
                WHEN 3 THEN 'GOOGLE_ADS'
                WHEN 4 THEN 'EMAIL_CAMPAIGN'
                WHEN 5 THEN 'PARTNER'
                WHEN 6 THEN 'ORGANIC_SEARCH'
                WHEN 7 THEN 'WEBINAR'
                ELSE 'DIRECT' END AS source_code,
           now() - (number || ' days')::interval AS created_on
    FROM generate_series(1, 96) AS generated(number)
), inserted AS (
    INSERT INTO business_partner (kind_id, status_id, lead_source_id, code, name, notes, created_at, updated_at)
    SELECT kind.id,
           status.id,
           source.id,
           seed.partner_code,
           seed.partner_name,
           'Sample CRM record generated for local development.',
           seed.created_on,
           seed.created_on + ((seed.number % 12) || ' hours')::interval
    FROM seed
    JOIN business_partner_kind kind ON kind.code = seed.kind_code
    JOIN business_partner_status status ON status.code = seed.status_code
    LEFT JOIN lead_source source ON source.code = seed.source_code
    WHERE NOT EXISTS (
        SELECT 1 FROM business_partner existing
        WHERE upper(existing.code) = upper(seed.partner_code) AND existing.archived_at IS NULL
    )
    RETURNING id, code, name, created_at
)
INSERT INTO business_partner_contact_method (business_partner_id, contact_method_type_id, label, value, primary_contact, created_at, updated_at)
SELECT inserted.id,
       type.id,
       'Primary',
       lower(replace(inserted.code, '-', '')) || '@example.test',
       TRUE,
       inserted.created_at,
       inserted.created_at
FROM inserted
JOIN contact_method_type type ON type.code = 'EMAIL';

WITH partners AS (
    SELECT id, code, name, created_at FROM business_partner WHERE archived_at IS NULL AND (code LIKE 'CUS-%' OR code LIKE 'LED-%' OR code LIKE 'SUP-%')
), phone_type AS (
    SELECT id FROM contact_method_type WHERE code = 'PHONE'
)
INSERT INTO business_partner_contact_method (business_partner_id, contact_method_type_id, label, value, primary_contact, created_at, updated_at)
SELECT partners.id,
       phone_type.id,
       'Office',
       '+49 7623 ' || lpad((100000 + partners.id)::text, 6, '0'),
       TRUE,
       partners.created_at,
       partners.created_at
FROM partners
CROSS JOIN phone_type
WHERE NOT EXISTS (
    SELECT 1 FROM business_partner_contact_method existing
    WHERE existing.business_partner_id = partners.id
      AND existing.contact_method_type_id = phone_type.id
      AND existing.archived_at IS NULL
);

WITH partners AS (
    SELECT id, code, name, created_at FROM business_partner WHERE archived_at IS NULL AND (code LIKE 'CUS-%' OR code LIKE 'LED-%' OR code LIKE 'SUP-%')
)
INSERT INTO business_partner_contact_person (
    business_partner_id, first_name, last_name, role_title, email, phone, mobile, department, primary_contact, notes, created_at, updated_at
)
SELECT partners.id,
       (ARRAY['Mira', 'Jonas', 'Sara', 'David', 'Lina', 'Omar', 'Nora', 'Leo'])[(partners.id % 8) + 1],
       (ARRAY['Meyer', 'Schneider', 'Fischer', 'Weber', 'Wagner', 'Becker', 'Hoffmann', 'Schulz'])[(partners.id % 8) + 1],
       CASE WHEN partners.id % 3 = 0 THEN 'Managing Director'
            WHEN partners.id % 3 = 1 THEN 'Operations Manager'
            ELSE 'Procurement Contact' END,
       lower(replace(partners.code, '-', '')) || '.contact@example.test',
       '+49 7623 ' || lpad((200000 + partners.id)::text, 6, '0'),
       CASE WHEN partners.id % 2 = 0 THEN '+49 171 ' || lpad((3000000 + partners.id)::text, 7, '0') ELSE NULL END,
       CASE WHEN partners.id % 3 = 0 THEN 'Management'
            WHEN partners.id % 3 = 1 THEN 'Operations'
            ELSE 'Purchasing' END,
       TRUE,
       'Primary sample contact person.',
       partners.created_at,
       partners.created_at
FROM partners
WHERE NOT EXISTS (
    SELECT 1 FROM business_partner_contact_person existing
    WHERE existing.business_partner_id = partners.id AND existing.archived_at IS NULL
);

WITH partners AS (
    SELECT id, code, created_at
    FROM business_partner
    WHERE archived_at IS NULL
      AND (code LIKE 'CUS-%' OR code LIKE 'LED-%' OR code LIKE 'SUP-%')
), address_source AS (
    SELECT partners.id AS business_partner_id,
           'Sample Street ' || partners.id AS line1,
           NULL::varchar AS line2,
           CASE WHEN partners.id % 4 = 0 THEN 'Zurich'
                WHEN partners.id % 4 = 1 THEN 'Basel'
                WHEN partners.id % 4 = 2 THEN 'Freiburg'
                ELSE 'Rheinfelden' END AS city,
           lpad((40000 + partners.id)::text, 5, '0') AS postal_code,
           CASE WHEN partners.id % 4 IN (0, 1) THEN 'CH' ELSE 'DE' END AS country_code,
           partners.created_at AS created_at
    FROM partners
    WHERE NOT EXISTS (
        SELECT 1
        FROM business_partner_address existing
        WHERE existing.business_partner_id = partners.id
          AND existing.archived_at IS NULL
    )
), inserted_addresses AS (
    INSERT INTO address (line1, line2, city, postal_code, country_code, created_at, updated_at)
    SELECT line1, line2, city, postal_code, country_code, created_at, created_at
    FROM address_source
    RETURNING id, line1
)
INSERT INTO business_partner_address (business_partner_id, address_id, address_type, primary_address, created_at, updated_at)
SELECT address_source.business_partner_id,
       inserted_addresses.id,
       'PRIMARY',
       TRUE,
       address_source.created_at,
       address_source.created_at
FROM inserted_addresses
JOIN address_source ON address_source.line1 = inserted_addresses.line1;

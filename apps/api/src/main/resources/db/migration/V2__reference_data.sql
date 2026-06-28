-- Copyright (c) Khaled Shawki. All rights reserved.

INSERT INTO business_partner_kind (code, name) VALUES
    ('CUSTOMER', 'Customer'),
    ('LEAD', 'Lead'),
    ('SUPPLIER', 'Supplier');

INSERT INTO business_partner_status (code, name) VALUES
    ('NEW', 'New'),
    ('ACTIVE', 'Active'),
    ('QUALIFIED', 'Qualified'),
    ('INACTIVE', 'Inactive');

INSERT INTO lead_source (code, name) VALUES
    ('WEBSITE', 'Website'),
    ('REFERRAL', 'Referral'),
    ('EVENT', 'Event'),
    ('DIRECT', 'Direct Contact');

INSERT INTO contact_method_type (code, name) VALUES
    ('EMAIL', 'Email'),
    ('PHONE', 'Phone'),
    ('WEBSITE', 'Website');

INSERT INTO document_type (code, name) VALUES
    ('GENERAL', 'General Document'),
    ('CONTRACT', 'Contract'),
    ('IDENTITY', 'Identity Document');

INSERT INTO security_role (code, name) VALUES
    ('ADMIN', 'Administrator'),
    ('USER', 'User');

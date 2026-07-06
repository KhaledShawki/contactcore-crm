-- Copyright (c) Khaled Shawki. All rights reserved.

create table commercial_item (
    id bigserial primary key,
    version bigint not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    archived_at timestamptz,
    source_system varchar(32) not null,
    source_tenant_id varchar(128) not null,
    external_id varchar(128) not null,
    item_code varchar(128) not null,
    name varchar(255) not null,
    description text,
    item_group varchar(128),
    unit_of_measure varchar(64),
    active boolean not null default true,
    last_synced_at timestamptz,
    constraint uq_commercial_item_source_identity unique (source_system, source_tenant_id, external_id),
    constraint ck_commercial_item_source_system check (source_system in ('CONTACTCORE', 'SAP_B1', 'EXTERNAL'))
);

create index ix_commercial_item_code on commercial_item (upper(item_code)) where archived_at is null;
create index ix_commercial_item_name on commercial_item (upper(name)) where archived_at is null;
create index ix_commercial_item_source on commercial_item (source_system, source_tenant_id) where archived_at is null;
create index ix_commercial_item_active on commercial_item (active) where archived_at is null;

create table commercial_document (
    id bigserial primary key,
    version bigint not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    archived_at timestamptz,
    source_system varchar(32) not null,
    source_tenant_id varchar(128) not null,
    external_id varchar(128) not null,
    external_number varchar(128) not null,
    type varchar(32) not null,
    status varchar(32) not null default 'UNKNOWN',
    source_status varchar(128),
    business_partner_id bigint references business_partner(id),
    business_partner_external_id varchar(128),
    business_partner_code_snapshot varchar(128),
    business_partner_name_snapshot varchar(255),
    document_date date not null,
    due_date date,
    delivery_date date,
    currency varchar(3) not null,
    subtotal_amount numeric(19, 6) not null default 0,
    discount_amount numeric(19, 6) not null default 0,
    tax_amount numeric(19, 6) not null default 0,
    total_amount numeric(19, 6) not null default 0,
    open_amount numeric(19, 6) not null default 0,
    last_synced_at timestamptz,
    constraint uq_commercial_document_source_identity unique (source_system, source_tenant_id, external_id),
    constraint ck_commercial_document_source_system check (source_system in ('CONTACTCORE', 'SAP_B1', 'EXTERNAL')),
    constraint ck_commercial_document_type check (type in ('SALES_QUOTATION', 'SALES_ORDER', 'DELIVERY_NOTE', 'CUSTOMER_INVOICE')),
    constraint ck_commercial_document_status check (status in ('OPEN', 'PARTIALLY_FULFILLED', 'FULFILLED', 'CLOSED', 'CANCELLED', 'UNKNOWN')),
    constraint ck_commercial_document_currency check (char_length(currency) = 3),
    constraint ck_commercial_document_amounts_non_negative check (
        subtotal_amount >= 0 and discount_amount >= 0 and tax_amount >= 0 and total_amount >= 0 and open_amount >= 0
    )
);

create index ix_commercial_document_bp on commercial_document (business_partner_id, document_date desc, id desc) where archived_at is null;
create index ix_commercial_document_type_status on commercial_document (type, status) where archived_at is null;
create index ix_commercial_document_source on commercial_document (source_system, source_tenant_id) where archived_at is null;
create index ix_commercial_document_number on commercial_document (upper(external_number)) where archived_at is null;
create index ix_commercial_document_date on commercial_document (document_date desc, id desc) where archived_at is null;

create table commercial_document_line (
    id bigserial primary key,
    version bigint not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    archived_at timestamptz,
    commercial_document_id bigint not null references commercial_document(id),
    source_line_id varchar(128) not null,
    line_number integer not null,
    item_id bigint references commercial_item(id),
    item_external_id varchar(128),
    item_code_snapshot varchar(128),
    item_name_snapshot varchar(255),
    description text,
    quantity numeric(19, 6) not null default 0,
    open_quantity numeric(19, 6) not null default 0,
    unit_of_measure varchar(64),
    unit_price numeric(19, 6) not null default 0,
    discount_percent numeric(9, 6) not null default 0,
    tax_code_snapshot varchar(64),
    line_total numeric(19, 6) not null default 0,
    currency varchar(3) not null,
    delivery_date date,
    constraint uq_commercial_document_line_source unique (commercial_document_id, source_line_id),
    constraint uq_commercial_document_line_number unique (commercial_document_id, line_number),
    constraint ck_commercial_document_line_currency check (char_length(currency) = 3),
    constraint ck_commercial_document_line_values_non_negative check (
        line_number >= 0 and quantity >= 0 and open_quantity >= 0 and unit_price >= 0 and discount_percent >= 0 and line_total >= 0
    )
);

create index ix_commercial_document_line_document on commercial_document_line (commercial_document_id, line_number, id) where archived_at is null;
create index ix_commercial_document_line_item on commercial_document_line (item_id) where archived_at is null;
create index ix_commercial_document_line_item_code on commercial_document_line (upper(item_code_snapshot)) where archived_at is null;

<!-- Copyright (c) Khaled Shawki. All rights reserved. -->

# ContactCore CRM

[![CI](https://github.com/KhaledShawki/contactcore-crm/actions/workflows/ci.yml/badge.svg)](https://github.com/KhaledShawki/contactcore-crm/actions/workflows/ci.yml)
![Java 25](https://img.shields.io/badge/Java-25-007396?logo=openjdk&logoColor=white)
![Spring Boot 3.5](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot&logoColor=white)
![React 19](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black)
![TypeScript 5.9](https://img.shields.io/badge/TypeScript-5.9-3178C6?logo=typescript&logoColor=white)
![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)

ContactCore CRM is a self-hostable CRM application for managing customers, leads, suppliers, marketing sources, contact persons, documents, reports, user profiles, and a read-only CRM assistant.

The project uses a Spring Boot API, a React frontend, PostgreSQL migrations, JWT authentication, S3-compatible file storage, Excel report export, Docker-based local deployment, and automated tests.

## Features

- Customer, lead, and supplier management
- Marketing-source management
- Contact-person management with primary-contact validation
- Business-partner document uploads through S3-compatible storage
- User profile and profile-image upload
- Dashboard with CRM KPIs
- CRM report page with marketing-source performance and contact coverage
- Backend-generated Excel exports for CRM lists and reports
- Read-only CRM assistant with deterministic tool planning and evidence-based answers
- Backend-owned UI schema for list and form configuration
- Soft archive instead of destructive delete
- Responsive React UI with light, dark, ocean, and graphite themes
- Docker Compose setup with PostgreSQL, MinIO, and optional ClamAV file scanning

## Architecture

```text
contactcore-crm/
├── apps/
│   ├── api/                 Spring Boot API
│   │   └── src/main/java/com/contactcore/
│   │       ├── analytics/    Dashboard and CRM report read models
│   │       ├── assistant/    Read-only CRM assistant, retrieval, audit, LLM gateway
│   │       ├── crm/          Business-partner domain, services, repositories, API
│   │       ├── marketing/    Marketing-source API and services
│   │       ├── profile/      Authenticated user profile API
│   │       ├── reports/      Backend-owned Excel report generation
│   │       ├── schema/       Backend-owned UI schema metadata
│   │       ├── security/     Login, JWT, users, roles, admin bootstrap
│   │       ├── storage/      S3-compatible file storage and metadata
│   │       └── shared/       Base entity, errors, page response, CORS
│   └── web/                 React + TypeScript + Vite frontend
│       └── src/
│           ├── analytics/    Dashboard and report endpoints
│           ├── api/          RTK Query base API
│           ├── assistant/    Assistant page, endpoints, and references
│           ├── auth/         Login flow and session state
│           ├── components/   Reusable UI primitives
│           ├── crm/          CRM endpoints and types
│           ├── layout/       Authenticated shell
│           ├── marketing/    Marketing-source endpoints
│           ├── notifications/ Toast-style user feedback
│           ├── pages/        Schema-rendered pages
│           ├── profile/      Profile endpoints
│           ├── reports/      Report download helpers
│           ├── schema/       UI schema types and helpers
│           ├── store/        Redux store
│           └── theme/        Theme and UI preferences
├── Dockerfile
├── docker-compose.yml
├── .env.example
└── README.md
```

## Backend

The backend is organized by business capability. Controllers stay thin, application services own use cases, repositories isolate persistence, and shared infrastructure is kept outside feature modules.

Main backend capabilities:

- Spring Boot 3.5 API
- PostgreSQL persistence with Flyway migrations
- JWT authentication and role-based user model
- BCrypt password hashing
- Admin bootstrap from environment variables
- Normalized CRM data model
- S3-compatible file storage through the AWS SDK
- Optional ClamAV file scanning for uploaded files
- Centralized API error handling
- Backend-owned Excel report generation with Apache POI
- Assistant request planning, tool validation, evidence gating, audit logging, and rate limiting

## Frontend

The frontend uses React, TypeScript, Vite, Redux Toolkit, RTK Query, and React Router.

Main frontend capabilities:

- Authenticated application shell
- Schema-rendered CRM list and form pages
- Server-side pagination and sorting
- Dirty-form detection for save buttons
- Single-flight protection for write actions
- Shared notification system for save, archive, upload, and export feedback
- Responsive tables and stacked mobile cards
- Theme, density, text-size, and sidebar preferences
- Blob-based report downloads from backend XLSX endpoints

## Database model

Customers, leads, and suppliers share the same `business_partner` model. Their type is stored through `business_partner_kind`, and statuses are stored through `business_partner_status`.

Core tables include:

- `app_user`
- `security_role`
- `app_user_role`
- `user_profile`
- `stored_file`
- `business_partner`
- `business_partner_kind`
- `business_partner_status`
- `lead_source`
- `contact_method_type`
- `business_partner_contact_method`
- `business_partner_contact_person`
- `address`
- `business_partner_address`
- `document_type`
- `business_partner_document`

Design choices:

- Shared customer, lead, and supplier data is stored once.
- Contact methods are normalized into typed email, phone, and website rows.
- Addresses are stored separately and linked to business partners.
- Uploaded file metadata is stored once and referenced by documents/profile images.
- Archive operations set `archived_at` instead of deleting rows.
- Report queries are backend-owned and do not depend on frontend table state.

## ContactCore Assistant

The assistant is read-only. It does not execute SQL generated by a model and does not perform write actions.

Assistant flow:

```text
User message
→ input guard and rate limit
→ deterministic intent classification
→ approved CRM tool plan
→ bounded CRM context
→ optional LLM phrasing
→ evidence-gated answer with record references
```

Supported examples:

- How many customers do we have?
- Which leads need follow-up?
- Which leads are missing contact persons?
- Show marketing-source performance.
- Find records related to Meyer.
- Do we have a customer named Example GmbH?

Non-CRM messages such as greetings or unclear text return static help responses without running CRM tools.

Assistant controls:

- JWT authentication required
- Per-user rate limiting
- Bounded input length
- Prompt-injection guard
- Approved CRM tools only
- Evidence gate for factual CRM answers
- Stored conversations, messages, references, and audit events
- `noop` provider by default for deterministic local development and tests
- Optional OpenAI-compatible HTTP provider

## Excel reports

Reports are generated on the backend and downloaded by the frontend as XLSX files.

Available exports:

- Customers
- Leads
- Suppliers
- Marketing sources
- CRM summary report

Backend report generation keeps filtering, row limits, filenames, headers, workbook structure, and data formatting server-side. The frontend only starts the request, downloads the blob, and displays success/error notifications.

## API overview

Authentication:

```text
POST /api/auth/login
GET  /api/auth/me
```

CRM:

```text
GET    /api/crm/business-partners?kind=CUSTOMER&q=&page=0&size=20&sort=updated_desc
GET    /api/crm/business-partners/{id}
POST   /api/crm/business-partners
PUT    /api/crm/business-partners/{id}
DELETE /api/crm/business-partners/{id}
```

Contact persons:

```text
GET    /api/crm/business-partners/{businessPartnerId}/contact-persons
POST   /api/crm/business-partners/{businessPartnerId}/contact-persons
PUT    /api/crm/business-partners/{businessPartnerId}/contact-persons/{contactPersonId}
DELETE /api/crm/business-partners/{businessPartnerId}/contact-persons/{contactPersonId}
```

Documents:

```text
GET    /api/crm/business-partners/{id}/documents
POST   /api/crm/business-partners/{id}/documents
DELETE /api/crm/business-partners/documents/{documentId}
```

Marketing sources:

```text
GET    /api/marketing/sources?q=&page=0&size=20
GET    /api/marketing/sources/options
GET    /api/marketing/sources/{id}
POST   /api/marketing/sources
PUT    /api/marketing/sources/{id}
DELETE /api/marketing/sources/{id}
```

Dashboard and analytics:

```text
GET /api/dashboard
GET /api/reports/crm
```

Excel reports:

```text
GET /api/reports/business-partners.xlsx?kind=CUSTOMER&q=&sort=updated_desc&maxRows=5000
GET /api/reports/business-partners.xlsx?kind=LEAD&q=&sort=updated_desc&maxRows=5000
GET /api/reports/business-partners.xlsx?kind=SUPPLIER&q=&sort=updated_desc&maxRows=5000
GET /api/reports/marketing-sources.xlsx?q=&maxRows=5000
GET /api/reports/crm-summary.xlsx
```

Profile:

```text
GET  /api/profile
PUT  /api/profile
POST /api/profile/image
```

UI schema:

```text
GET /api/ui/manifest
GET /api/ui/screens/{screenKey}
```

Assistant:

```text
POST   /api/assistant/messages
GET    /api/assistant/conversations
GET    /api/assistant/conversations/{conversationId}
DELETE /api/assistant/conversations/{conversationId}
```

## Local development

Start PostgreSQL, MinIO, and ClamAV:

```bash
docker compose up postgres minio clamav
```

Run the API:

```bash
cd apps/api
mvn spring-boot:run
```

Run the API with optional demo data:

```bash
cd apps/api
SPRING_PROFILES_ACTIVE=demo mvn spring-boot:run
```

Run the frontend:

```bash
cd apps/web
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

The local admin user is created from environment variables. When using `.env.example`, the default username is `admin` and the password is `change-this-password`.

## Docker run

Copy the environment template:

```bash
cp .env.example .env
```

Edit `.env` before starting the stack, especially:

```text
CONTACTCORE_ADMIN_PASSWORD
CONTACTCORE_JWT_SECRET
POSTGRES_PASSWORD
CONTACTCORE_S3_SECRET_KEY
```

Start everything:

```bash
docker compose up --build
```

Start everything with optional demo data loaded through the `demo` Spring profile:

```bash
SPRING_PROFILES_ACTIVE=demo docker compose up --build
```

Open the web app:

```text
http://localhost:5173
```

MinIO console:

```text
http://localhost:9001
```

## Assistant provider configuration

The default assistant provider is `noop`.

```bash
CONTACTCORE_ASSISTANT_ENABLED=true
CONTACTCORE_ASSISTANT_PROVIDER=noop
CONTACTCORE_ASSISTANT_MODEL=contactcore-noop
CONTACTCORE_ASSISTANT_ENDPOINT=
CONTACTCORE_ASSISTANT_API_KEY=
CONTACTCORE_ASSISTANT_TIMEOUT_MS=20000
CONTACTCORE_ASSISTANT_MAX_CONTEXT_CHARS=12000
CONTACTCORE_ASSISTANT_MAX_RESULTS=20
CONTACTCORE_ASSISTANT_RATE_LIMIT_PER_MINUTE=10
```

For an OpenAI-compatible HTTP endpoint:

```bash
CONTACTCORE_ASSISTANT_PROVIDER=generic-http
CONTACTCORE_ASSISTANT_MODEL=qwen3-0.6b
CONTACTCORE_ASSISTANT_ENDPOINT=http://host.docker.internal:8081/v1/chat/completions
CONTACTCORE_ASSISTANT_API_KEY=replace-with-provider-api-key
CONTACTCORE_ASSISTANT_TIMEOUT_MS=60000
```

## Quality and testing

The project includes automated checks for backend behavior, frontend behavior, build correctness, linting, and React-specific diagnostics. Coverage is described by area instead of a percentage because no generated coverage report is committed.

Backend tests cover:

- Assistant intent classification, query planning, tool-plan validation, evidence gating, and deterministic answers
- Authentication token handling and assistant rate limiting
- CRM input normalization and contact-person validation
- File-upload validation and scanning rules
- API error handling
- Excel report SQL template generation
- Excel workbook generation, including large streaming exports

Frontend tests cover:

- Form dirty-state comparison
- Single-flight report download handling
- Notification behavior
- Assistant UI behavior
- Schema-driven form and list behavior

API checks:

```bash
cd apps/api
mvn test
```

Repository hygiene check:

```bash
./scripts/check-repo-hygiene.sh
```

Frontend checks:

```bash
cd apps/web
npm ci
npm run lint
npm run test
npm run build
npm run doctor:react -- --verbose --yes --no-score
```

Useful manual checks before deploying or sharing a build:

- Login and logout
- Customer, lead, and supplier search/sort/pagination
- Create, edit, and archive CRM records
- Primary contact conflict handling
- Document upload and archive
- Profile update and image upload
- Excel export for customers, leads, suppliers, marketing sources, and CRM summary
- Assistant greeting, unclear message, CRM count question, and record search question

## Portfolio publishing checklist

Before publishing this repository, run:

```bash
./scripts/check-repo-hygiene.sh
```

The repository should not contain local secrets, IDE files, generated frontend builds, Vite caches, macOS metadata, or archive metadata. Keep only `.env.example`; never commit `.env`.

Recommended portfolio assets:

- `docs/screenshots/dashboard.png`
- `docs/screenshots/business-partners.png`
- `docs/screenshots/assistant.png`
- `docs/architecture.md`
- `docs/api.md` or generated OpenAPI docs
- `docs/security.md` for authentication, upload, and assistant constraints


## Runtime versions

- Java 25
- Spring Boot 3.5.x
- Node 22
- React 19
- PostgreSQL 16
- MinIO or another S3-compatible storage endpoint

## Notes

- `.env` files are local-only.
- `.env.example` is safe to commit and documents the expected settings.
- Sample seed data is intended for local development.
- Uploaded files are stored outside the repository through S3-compatible storage.

## CRM connector architecture

ContactCore can run against external CRM backends through a connector layer. ContactCore IAM remains the primary authentication and authorization boundary. Users sign in to ContactCore first, then select an allowed CRM connector instance. When an external connector session is active, business-partner list and detail screens read from the selected connector instead of the local ContactCore `business_partner` tables.

The first connector implementation is SAP Business One Service Layer. It is read-only in this release and supports SAP Business Partner lookup through connector-neutral endpoints:

```text
GET    /api/connectors/instances
GET    /api/connectors/session
POST   /api/connectors/session
DELETE /api/connectors/session
GET    /api/connectors/business-partners?q=&type=&page=&size=&sort=
GET    /api/connectors/business-partners/{externalId}
```

Internally the connector layer now uses a connector-neutral Business Partner model with identity, classification, status, contact points, addresses, contact persons, financial profile, commercial profile, transaction summary placeholders, and source references. SAP B1 `BusinessPartners` records are mapped into this Business Partner model by the SAP adapter; raw SAP DTOs are not exposed to the frontend or assistant.

The SAP B1 adapter is split into resource gateways and a transport-only Service Layer client port. `SapB1ServiceLayerClient` is the resource-neutral client contract, and `DefaultSapB1ServiceLayerClient` is the Spring-managed transport adapter that owns HTTP calls, login/logout, cookies, error mapping, and JSON serialization. `SapB1BusinessPartnerGateway` owns the SAP `BusinessPartners` resource path/query construction. `SapB1BusinessPartnerMapper` maps SAP fields into connector Business Partner summaries/details. This prevents the Service Layer client from becoming a large resource-specific class as Items, activities, orders, invoices, and payments are added later.

SAP B1 credentials and Service Layer cookies are never sent to the browser. The browser receives only connector status metadata. The backend stores the active SAP session in memory per ContactCore user. SAP passwords are used only for the login request and are not persisted.

Connector instances are configured server-side in `crm_connector_instance`. User access is granted through `crm_connector_user_access`. A SAP B1 connector instance uses JSON configuration like:

```json
{
  "serviceLayerBaseUrl": "https://sap-server:50000/b1s/v2",
  "companyDb": "SBODEMO_DE",
  "timeoutMs": 30000
}
```

Example setup for a development database:

```sql
insert into crm_connector_instance (type, display_name, environment, config_json, enabled)
values (
  'SAP_B1',
  'SAP B1 Demo',
  'TEST',
  '{"serviceLayerBaseUrl":"https://sap-server:50000/b1s/v2","companyDb":"SBODEMO_DE","timeoutMs":30000}',
  true
);

insert into crm_connector_user_access (user_id, connector_instance_id, can_read_business_partners, enabled)
select user_account.id, connector.id, true, true
from app_user user_account
cross join crm_connector_instance connector
where user_account.username = 'admin'
  and connector.display_name = 'SAP B1 Demo';
```

The assistant remains read-only and evidence-gated. Business Partner search and detail requests use the active CRM connector when one is connected. If no connector is active, the existing local ContactCore CRM tools continue to serve local data.

The Business Partner model is intentionally broader than the first SAP B1 screen. It is the anchor for the next reporting phase, where optional connector snapshots can link business partners to addresses, contact persons, financial balances, sales orders, purchase orders, AR/AP invoices, payments, activities, and graph-style business insights without reworking the connector API.

### Assistant multilingual policy

The assistant uses a language-independent planning pipeline. The latest user message is analyzed to choose the assistant response language. When English, German, or Arabic is confidently detected, the answer follows that message language even if the selected application language is different. If the message language is unclear or mixed without a dominant command language, the assistant falls back to the selected application locale.

Tool planning remains canonical and language-neutral. User phrases such as `customer`, `Kunde`, and `عميل` normalize to the same internal `CUSTOMER` concept before tool execution. Tool arguments use stable enums and identifiers, not translated labels. Tool results remain structured facts, and source data such as SAP codes, company names, emails, phone numbers, document numbers, and external record values is preserved unchanged. Only assistant explanation text and owned labels are localized.

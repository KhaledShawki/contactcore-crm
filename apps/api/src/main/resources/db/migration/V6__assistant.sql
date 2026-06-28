-- Copyright (c) Khaled Shawki. All rights reserved.

CREATE TABLE assistant_conversation (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    title VARCHAR(160) NOT NULL
);

CREATE TABLE assistant_message (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    conversation_id BIGINT NOT NULL REFERENCES assistant_conversation(id) ON DELETE CASCADE,
    role VARCHAR(32) NOT NULL,
    content TEXT NOT NULL
);

CREATE TABLE assistant_message_reference (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    message_id BIGINT NOT NULL REFERENCES assistant_message(id) ON DELETE CASCADE,
    entity_type VARCHAR(64) NOT NULL,
    entity_id BIGINT NOT NULL,
    label VARCHAR(255) NOT NULL,
    route VARCHAR(255) NOT NULL
);

CREATE TABLE assistant_audit_event (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    archived_at TIMESTAMPTZ,
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    conversation_id BIGINT REFERENCES assistant_conversation(id) ON DELETE SET NULL,
    request_type VARCHAR(64) NOT NULL,
    retrieval_count INTEGER NOT NULL CHECK (retrieval_count >= 0),
    model_name VARCHAR(128) NOT NULL,
    success BOOLEAN NOT NULL,
    failure_reason VARCHAR(500)
);

CREATE INDEX idx_assistant_conversation_user_active
    ON assistant_conversation(user_id, updated_at DESC, id DESC)
    WHERE archived_at IS NULL;

CREATE INDEX idx_assistant_message_conversation
    ON assistant_message(conversation_id, id ASC);

CREATE INDEX idx_assistant_reference_message
    ON assistant_message_reference(message_id);

CREATE INDEX idx_assistant_audit_user_created
    ON assistant_audit_event(user_id, created_at DESC);

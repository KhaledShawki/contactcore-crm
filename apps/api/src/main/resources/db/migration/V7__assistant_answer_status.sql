-- Copyright (c) Khaled Shawki. All rights reserved.

ALTER TABLE assistant_audit_event
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'SUCCESS',
    ADD COLUMN answer_source VARCHAR(32);

UPDATE assistant_audit_event
SET status = CASE WHEN success THEN 'SUCCESS' ELSE 'FAILED' END,
    answer_source = CASE WHEN success THEN 'LLM' ELSE NULL END;

ALTER TABLE assistant_audit_event
    ALTER COLUMN status DROP DEFAULT;

CREATE INDEX idx_assistant_audit_status_created
    ON assistant_audit_event(status, created_at DESC);

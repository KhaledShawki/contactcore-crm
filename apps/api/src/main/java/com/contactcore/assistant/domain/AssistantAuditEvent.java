// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "assistant_audit_event")
public class AssistantAuditEvent extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "conversation_id")
    private Long conversationId;

    @Column(name = "request_type", nullable = false, length = 64)
    private String requestType;

    @Column(name = "retrieval_count", nullable = false)
    private int retrievalCount;

    @Column(name = "model_name", nullable = false, length = 128)
    private String modelName;

    @Column(nullable = false)
    private boolean success;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "answer_source", length = 32)
    private String answerSource;

    protected AssistantAuditEvent() {}

    public AssistantAuditEvent(Long userId, Long conversationId, String requestType, int retrievalCount,
                               String modelName, boolean success, String failureReason) {
        this(userId, conversationId, requestType, retrievalCount, modelName, success, failureReason,
                success ? "SUCCESS" : "FAILED", null);
    }

    public AssistantAuditEvent(Long userId, Long conversationId, String requestType, int retrievalCount,
                               String modelName, boolean success, String failureReason,
                               String status, String answerSource) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.requestType = requestType;
        this.retrievalCount = retrievalCount;
        this.modelName = modelName;
        this.success = success;
        this.failureReason = failureReason;
        this.status = status;
        this.answerSource = answerSource;
    }
}

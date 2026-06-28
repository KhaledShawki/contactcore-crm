// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "assistant_message_reference")
public class AssistantMessageReference extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private AssistantMessage message;

    @Column(name = "entity_type", nullable = false, length = 64)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 255)
    private String label;

    @Column(nullable = false, length = 255)
    private String route;

    protected AssistantMessageReference() {}

    public AssistantMessageReference(String entityType, Long entityId, String label, String route) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.label = label;
        this.route = route;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getLabel() {
        return label;
    }

    public String getRoute() {
        return route;
    }

    void attachTo(AssistantMessage message) {
        this.message = message;
    }
}

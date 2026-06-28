// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "assistant_conversation")
public class AssistantConversation extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 160)
    private String title;

    protected AssistantConversation() {}

    public AssistantConversation(Long userId, String title) {
        this.userId = userId;
        this.title = title;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public void rename(String title) {
        this.title = title;
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assistant_message")
public class AssistantMessage extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private AssistantConversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AssistantMessageRole role;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssistantMessageReference> references = new ArrayList<>();

    protected AssistantMessage() {}

    public AssistantMessage(AssistantConversation conversation, AssistantMessageRole role, String content) {
        this.conversation = conversation;
        this.role = role;
        this.content = content;
    }

    public AssistantConversation getConversation() {
        return conversation;
    }

    public AssistantMessageRole getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public List<AssistantMessageReference> getReferences() {
        return references;
    }

    public void addReference(AssistantMessageReference reference) {
        references.add(reference);
        reference.attachTo(this);
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssistantConversationRepository extends JpaRepository<AssistantConversation, Long> {
    @Query("""
            select conversation from AssistantConversation conversation
            where conversation.archivedAt is null and conversation.userId = :userId
            order by conversation.updatedAt desc, conversation.id desc
            """)
    List<AssistantConversation> findActiveByUserId(@Param("userId") Long userId);

    @Query("""
            select conversation from AssistantConversation conversation
            where conversation.archivedAt is null and conversation.userId = :userId and conversation.id = :id
            """)
    Optional<AssistantConversation> findActiveByUserIdAndId(@Param("userId") Long userId, @Param("id") Long id);
}

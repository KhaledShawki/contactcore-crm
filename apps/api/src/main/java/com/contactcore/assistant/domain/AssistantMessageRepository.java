// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssistantMessageRepository extends JpaRepository<AssistantMessage, Long> {
    @Query("""
            select distinct message from AssistantMessage message
            left join fetch message.references
            where message.conversation.id = :conversationId
            order by message.id asc
            """)
    List<AssistantMessage> findConversationMessages(@Param("conversationId") Long conversationId);
}

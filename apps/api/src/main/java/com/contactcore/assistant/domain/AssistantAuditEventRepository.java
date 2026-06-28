// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssistantAuditEventRepository extends JpaRepository<AssistantAuditEvent, Long> {}

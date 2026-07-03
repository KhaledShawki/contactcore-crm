// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CrmConnectorAuditEventRepository extends JpaRepository<CrmConnectorAuditEvent, Long> {}

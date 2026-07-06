// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IamAuditEventRepository extends JpaRepository<IamAuditEvent, Long> {}

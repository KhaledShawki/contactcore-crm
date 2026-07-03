// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrmConnectorInstanceRepository extends JpaRepository<CrmConnectorInstance, Long> {
    Optional<CrmConnectorInstance> findByIdAndEnabledTrueAndArchivedAtIsNull(Long id);
}

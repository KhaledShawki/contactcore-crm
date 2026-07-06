// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CrmConnectorUserAccessRepository extends JpaRepository<CrmConnectorUserAccess, Long> {
    @Query("""
            select access
            from CrmConnectorUserAccess access
            join fetch access.connectorInstance instance
            where access.user.id = :userId
              and access.enabled = true
              and access.archivedAt is null
              and instance.enabled = true
              and instance.archivedAt is null
            order by instance.sortOrder asc, instance.displayName asc
            """)
    List<CrmConnectorUserAccess> findEnabledAccessForUser(@Param("userId") Long userId);

    @Query("""
            select access
            from CrmConnectorUserAccess access
            join fetch access.connectorInstance instance
            where access.user.id = :userId
              and instance.id = :instanceId
              and access.enabled = true
              and access.archivedAt is null
              and instance.enabled = true
              and instance.archivedAt is null
            """)
    Optional<CrmConnectorUserAccess> findEnabledAccess(@Param("userId") Long userId, @Param("instanceId") Long instanceId);

    @Query("""
            select access
            from CrmConnectorUserAccess access
            where access.user.id = :userId
              and access.connectorInstance.id = :instanceId
              and access.archivedAt is null
            """)
    Optional<CrmConnectorUserAccess> findActiveAccess(@Param("userId") Long userId,
                                                      @Param("instanceId") Long instanceId);
}

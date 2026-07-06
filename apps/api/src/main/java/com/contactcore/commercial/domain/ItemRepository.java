// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
    @Query("""
            select item from Item item
            where item.archivedAt is null and item.id = :id
            """)
    Optional<Item> findActiveById(@Param("id") Long id);

    @Query("""
            select item from Item item
            where item.archivedAt is null
              and item.sourceSystem = :sourceSystem
              and item.sourceTenantId = :sourceTenantId
              and item.externalId = :externalId
            """)
    Optional<Item> findActiveBySourceIdentity(
            @Param("sourceSystem") CommercialSourceSystem sourceSystem,
            @Param("sourceTenantId") String sourceTenantId,
            @Param("externalId") String externalId
    );
}

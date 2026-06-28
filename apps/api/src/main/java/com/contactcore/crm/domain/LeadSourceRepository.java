// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeadSourceRepository extends JpaRepository<LeadSource, Long> {
    Optional<LeadSource> findByCodeIgnoreCase(String code);

    @Query("""
            select source from LeadSource source
            where source.archivedAt is null
              and (:query = ''
                   or lower(source.code) like lower(concat('%', :query, '%'))
                   or lower(source.name) like lower(concat('%', :query, '%')))
            """)
    Page<LeadSource> searchActive(@Param("query") String query, Pageable pageable);

    @Query("""
            select source from LeadSource source
            where source.archivedAt is null
            order by source.sortOrder asc, source.name asc
            """)
    List<LeadSource> findActiveOrdered();

    @Query("""
            select source from LeadSource source
            where source.archivedAt is null and source.id = :id
            """)
    Optional<LeadSource> findActiveById(@Param("id") Long id);

    @Query("""
            select source from LeadSource source
            where source.archivedAt is null and upper(source.code) = upper(:code)
            """)
    Optional<LeadSource> findActiveByCode(@Param("code") String code);
}

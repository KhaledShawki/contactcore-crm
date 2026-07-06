// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommercialDocumentRepository extends JpaRepository<CommercialDocument, Long>, JpaSpecificationExecutor<CommercialDocument> {
    @Query("""
            select document from CommercialDocument document
            where document.archivedAt is null and document.id = :id
            """)
    Optional<CommercialDocument> findActiveById(@Param("id") Long id);

    @Query("""
            select document from CommercialDocument document
            where document.archivedAt is null
              and document.sourceSystem = :sourceSystem
              and document.sourceTenantId = :sourceTenantId
              and document.externalId = :externalId
            """)
    Optional<CommercialDocument> findActiveBySourceIdentity(
            @Param("sourceSystem") CommercialSourceSystem sourceSystem,
            @Param("sourceTenantId") String sourceTenantId,
            @Param("externalId") String externalId
    );

    @EntityGraph(attributePaths = {"businessPartner", "lines", "lines.item"})
    @Query("""
            select distinct document from CommercialDocument document
            where document.archivedAt is null and document.id = :id
            """)
    Optional<CommercialDocument> findActiveDetailById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"businessPartner"})
    @Query("""
            select document from CommercialDocument document
            where document.archivedAt is null
              and document.businessPartner.id = :businessPartnerId
            order by document.documentDate desc, document.id desc
            """)
    List<CommercialDocument> findRecentActiveByBusinessPartnerId(@Param("businessPartnerId") Long businessPartnerId, Pageable pageable);

    long countByBusinessPartner_IdAndArchivedAtIsNull(Long businessPartnerId);

    long countByBusinessPartner_IdAndArchivedAtIsNullAndStatusIn(Long businessPartnerId, Collection<CommercialDocumentStatus> statuses);

    long countByBusinessPartner_IdAndArchivedAtIsNullAndType(Long businessPartnerId, CommercialDocumentType type);

    long countByBusinessPartner_IdAndArchivedAtIsNullAndTypeAndStatusIn(
            Long businessPartnerId,
            CommercialDocumentType type,
            Collection<CommercialDocumentStatus> statuses
    );

    @Query("""
            select max(document.documentDate)
            from CommercialDocument document
            where document.archivedAt is null
              and document.businessPartner.id = :businessPartnerId
            """)
    Optional<LocalDate> latestDocumentDate(@Param("businessPartnerId") Long businessPartnerId);

    @Query("""
            select document.currency as currency,
                   count(document.id) as documentCount,
                   sum(document.totalAmount) as totalAmount,
                   sum(document.openAmount) as openAmount
            from CommercialDocument document
            where document.archivedAt is null
              and document.businessPartner.id = :businessPartnerId
            group by document.currency
            order by document.currency asc
            """)
    List<CommercialAmountByCurrencyProjection> totalAmountsByCurrency(@Param("businessPartnerId") Long businessPartnerId);
}

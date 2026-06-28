// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusinessPartnerDocumentRepository extends JpaRepository<BusinessPartnerDocument, Long> {
    @Query("""
            select document from BusinessPartnerDocument document
            join fetch document.storedFile
            join fetch document.documentType
            where document.businessPartner.id = :businessPartnerId and document.archivedAt is null
            order by document.createdAt desc
            """)
    List<BusinessPartnerDocument> findActiveByBusinessPartnerId(@Param("businessPartnerId") Long businessPartnerId);

    @Query("""
            select document from BusinessPartnerDocument document
            where document.id = :id and document.archivedAt is null
            """)
    Optional<BusinessPartnerDocument> findActiveById(@Param("id") Long id);
}

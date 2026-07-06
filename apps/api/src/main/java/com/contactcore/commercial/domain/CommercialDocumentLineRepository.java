// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.domain;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommercialDocumentLineRepository extends JpaRepository<CommercialDocumentLine, Long> {
    @EntityGraph(attributePaths = {"item"})
    @Query("""
            select line from CommercialDocumentLine line
            where line.archivedAt is null
              and line.commercialDocument.archivedAt is null
              and line.commercialDocument.id = :documentId
            order by line.lineNumber asc, line.id asc
            """)
    List<CommercialDocumentLine> findActiveByDocumentId(@Param("documentId") Long documentId);
}

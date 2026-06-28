// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.marketing;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
class MarketingSourceReportQueryService {
    private static final int DEFAULT_LIMIT = 5000;
    private static final int MAX_LIMIT = 10000;

    private final EntityManager entityManager;
    private final MarketingSourceReportSqlBuilder sqlBuilder;

    MarketingSourceReportQueryService(EntityManager entityManager, MarketingSourceReportSqlBuilder sqlBuilder) {
        this.entityManager = entityManager;
        this.sqlBuilder = sqlBuilder;
    }

    List<MarketingSourceReportRow> search(String queryText, int maxRows) {
        Query query = entityManager.createNativeQuery(sqlBuilder.build());
        query.setParameter("query", normalize(queryText));
        query.setParameter("limit", normalizeLimit(maxRows));
        return rows(query).stream().map(this::toRow).toList();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> rows(Query query) {
        return query.getResultList();
    }

    private MarketingSourceReportRow toRow(Object[] row) {
        return new MarketingSourceReportRow(
                text(row[0]),
                text(row[1]),
                number(row[2]),
                number(row[3]),
                number(row[4]),
                number(row[5]),
                number(row[6]),
                instant(row[7]),
                instant(row[8])
        );
    }

    private int normalizeLimit(int maxRows) {
        if (maxRows <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(maxRows, MAX_LIMIT);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private Integer number(Object value) {
        return value instanceof Number number ? number.intValue() : 0;
    }

    private Instant instant(Object value) {
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant();
        }
        if (value instanceof Instant instant) {
            return instant;
        }
        return null;
    }
}

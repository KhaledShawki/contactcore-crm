// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.crm;

import com.contactcore.shared.api.InvalidRequestException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Repository;

@Repository
class BusinessPartnerReportQueryService {
    private static final int DEFAULT_LIMIT = 5000;
    private static final int MAX_LIMIT = 10000;

    private final EntityManager entityManager;
    private final BusinessPartnerReportSqlBuilder sqlBuilder;

    BusinessPartnerReportQueryService(EntityManager entityManager, BusinessPartnerReportSqlBuilder sqlBuilder) {
        this.entityManager = entityManager;
        this.sqlBuilder = sqlBuilder;
    }

    List<BusinessPartnerReportRow> search(String kindCode, String queryText, String sort, int maxRows) {
        String kind = normalizeKind(kindCode);
        Query query = entityManager.createNativeQuery(sqlBuilder.build(orderBy(sort)));
        query.setParameter("kindCode", kind);
        query.setParameter("query", normalize(queryText));
        query.setParameter("limit", normalizeLimit(maxRows));
        return rows(query).stream().map(this::toRow).toList();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> rows(Query query) {
        return query.getResultList();
    }

    private BusinessPartnerReportRow toRow(Object[] row) {
        return new BusinessPartnerReportRow(
                text(row[0]),
                text(row[1]),
                text(row[2]),
                text(row[3]),
                text(row[4]),
                text(row[5]),
                text(row[6]),
                text(row[7]),
                text(row[8]),
                number(row[9]),
                number(row[10]),
                text(row[11]),
                text(row[12]),
                instant(row[13]),
                instant(row[14]),
                text(row[15])
        );
    }

    private String orderBy(String sort) {
        String value = sort == null ? "" : sort.trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case "created_desc" -> "partner.created_at desc, partner.id desc";
            case "name_asc" -> "partner.name asc, partner.id asc";
            case "code_asc" -> "partner.code asc, partner.id asc";
            case "status_asc" -> "status.name asc, partner.name asc, partner.id asc";
            case "updated_desc", "" -> "partner.updated_at desc, partner.id desc";
            default -> throw new InvalidRequestException("Unsupported report sort: " + sort);
        };
    }

    private String normalizeKind(String kindCode) {
        if (kindCode == null || kindCode.isBlank()) {
            throw new InvalidRequestException("Business partner kind is required for report export.");
        }
        String kind = kindCode.trim().toUpperCase(Locale.ROOT);
        if (!kind.equals("CUSTOMER") && !kind.equals("LEAD") && !kind.equals("SUPPLIER")) {
            throw new InvalidRequestException("Unsupported business partner kind for report export: " + kindCode);
        }
        return kind;
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

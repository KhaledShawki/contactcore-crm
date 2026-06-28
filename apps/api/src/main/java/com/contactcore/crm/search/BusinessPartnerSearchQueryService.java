// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.search;

import com.contactcore.crm.api.BusinessPartnerResponse;
import com.contactcore.shared.api.PageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class BusinessPartnerSearchQueryService {
    private final EntityManager entityManager;
    private final BusinessPartnerSearchSqlBuilder sqlBuilder;

    BusinessPartnerSearchQueryService(EntityManager entityManager, BusinessPartnerSearchSqlBuilder sqlBuilder) {
        this.entityManager = entityManager;
        this.sqlBuilder = sqlBuilder;
    }

    public PageResponse<BusinessPartnerResponse> search(BusinessPartnerSearchCriteria criteria) {
        List<BusinessPartnerResponse> items = rows(searchQuery(criteria)).stream()
                .map(this::toResponse)
                .toList();
        long total = count(countQuery(criteria));
        int totalPages = (int) Math.ceil((double) total / criteria.size());
        return new PageResponse<>(items, criteria.page(), criteria.size(), total, totalPages);
    }

    private Query searchQuery(BusinessPartnerSearchCriteria criteria) {
        Query query = entityManager.createNativeQuery(sqlBuilder.searchSql(criteria));
        bindCommonParameters(query, criteria);
        query.setParameter("limit", criteria.size());
        query.setParameter("offset", criteria.offset());
        return query;
    }

    private Query countQuery(BusinessPartnerSearchCriteria criteria) {
        Query query = entityManager.createNativeQuery(sqlBuilder.countSql(criteria));
        bindCommonParameters(query, criteria);
        return query;
    }

    private void bindCommonParameters(Query query, BusinessPartnerSearchCriteria criteria) {
        query.setParameter("kindCode", criteria.kindCode());
        if (criteria.hasQuery()) {
            query.setParameter("queryPattern", criteria.queryPattern());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> rows(Query query) {
        return query.getResultList();
    }

    private long count(Query query) {
        Object value = query.getSingleResult();
        return value instanceof Number number ? number.longValue() : 0L;
    }

    private BusinessPartnerResponse toResponse(Object[] row) {
        return new BusinessPartnerResponse(
                number(row[0]),
                number(row[1]),
                instant(row[2]),
                instant(row[3]),
                text(row[4]),
                text(row[5]),
                text(row[6]),
                nullableText(row[7]),
                text(row[8]),
                text(row[9]),
                nullableText(row[10]),
                nullableText(row[11]),
                nullableText(row[12]),
                nullableText(row[13]),
                nullableText(row[14]),
                nullableText(row[15]),
                nullableText(row[16]),
                nullableText(row[17]),
                nullableText(row[18]),
                List.of(),
                List.of()
        );
    }

    private Long number(Object value) {
        return value instanceof Number number ? number.longValue() : null;
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String nullableText(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Instant instant(Object value) {
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant();
        }
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toInstant();
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.atZone(java.time.ZoneOffset.UTC).toInstant();
        }
        return null;
    }
}

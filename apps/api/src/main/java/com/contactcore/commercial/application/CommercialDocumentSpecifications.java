// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import com.contactcore.commercial.domain.CommercialDocument;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

final class CommercialDocumentSpecifications {
    private CommercialDocumentSpecifications() {}

    static Specification<CommercialDocument> matching(CommercialDocumentSearchCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.isNull(root.get("archivedAt")));

            if (criteria.businessPartnerId() != null) {
                predicates.add(builder.equal(root.get("businessPartner").get("id"), criteria.businessPartnerId()));
            }
            if (criteria.type() != null) {
                predicates.add(builder.equal(root.get("type"), criteria.type()));
            }
            if (criteria.status() != null) {
                predicates.add(builder.equal(root.get("status"), criteria.status()));
            }
            if (criteria.sourceSystem() != null) {
                predicates.add(builder.equal(root.get("sourceSystem"), criteria.sourceSystem()));
            }
            if (criteria.fromDate() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("documentDate"), criteria.fromDate()));
            }
            if (criteria.toDate() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("documentDate"), criteria.toDate()));
            }
            if (criteria.hasQuery()) {
                String pattern = "%" + criteria.query().trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("externalNumber")), pattern),
                        builder.like(builder.lower(root.get("businessPartnerExternalId")), pattern),
                        builder.like(builder.lower(root.get("businessPartnerCodeSnapshot")), pattern),
                        builder.like(builder.lower(root.get("businessPartnerNameSnapshot")), pattern),
                        builder.like(builder.lower(root.get("sourceStatus")), pattern)
                ));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}

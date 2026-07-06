// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import com.contactcore.commercial.domain.Item;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

final class ItemSpecifications {
    private ItemSpecifications() {}

    static Specification<Item> matching(ItemSearchCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.isNull(root.get("archivedAt")));
            if (criteria.sourceSystem() != null) {
                predicates.add(builder.equal(root.get("sourceSystem"), criteria.sourceSystem()));
            }
            if (criteria.active() != null) {
                predicates.add(builder.equal(root.get("active"), criteria.active()));
            }
            if (criteria.hasQuery()) {
                String pattern = "%" + criteria.query().trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("itemCode")), pattern),
                        builder.like(builder.lower(root.get("name")), pattern),
                        builder.like(builder.lower(root.get("description")), pattern),
                        builder.like(builder.lower(root.get("itemGroup")), pattern),
                        builder.like(builder.lower(root.get("externalId")), pattern)
                ));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}

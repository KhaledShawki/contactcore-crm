// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import com.contactcore.commercial.api.ItemDetailResponse;
import com.contactcore.commercial.api.ItemSummaryResponse;
import com.contactcore.commercial.domain.CommercialSourceSystem;
import com.contactcore.commercial.domain.ItemRepository;
import com.contactcore.shared.api.NotFoundException;
import com.contactcore.shared.api.PageRequestNormalizer;
import com.contactcore.shared.api.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemQueryService {
    private final ItemRepository items;

    public ItemQueryService(ItemRepository items) {
        this.items = items;
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemSummaryResponse> search(CommercialSourceSystem sourceSystem, Boolean active, String query,
                                                    int page, int size, String sort) {
        int normalizedPage = PageRequestNormalizer.page(page);
        int normalizedSize = PageRequestNormalizer.size(size);
        String normalizedQuery = PageRequestNormalizer.query(query);
        ItemSearchCriteria criteria = new ItemSearchCriteria(sourceSystem, active, normalizedQuery);
        var request = PageRequest.of(normalizedPage, normalizedSize, ItemSort.from(sort).sort());
        return PageResponse.from(items.findAll(ItemSpecifications.matching(criteria), request)
                .map(CommercialMapper::toItemSummaryResponse));
    }

    @Transactional(readOnly = true)
    public ItemDetailResponse get(Long id) {
        return CommercialMapper.toItemDetailResponse(items.findActiveById(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id)));
    }
}

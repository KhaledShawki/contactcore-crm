// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.marketing.application;

import com.contactcore.crm.domain.LeadSource;
import com.contactcore.crm.domain.LeadSourceRepository;
import com.contactcore.marketing.api.MarketingSourceResponse;
import com.contactcore.marketing.api.MarketingSourceWriteRequest;
import com.contactcore.shared.api.InvalidRequestException;
import com.contactcore.shared.api.NotFoundException;
import com.contactcore.shared.api.PageResponse;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketingSourceService {
    private final LeadSourceRepository sources;

    public MarketingSourceService(LeadSourceRepository sources) {
        this.sources = sources;
    }

    @Transactional(readOnly = true)
    public PageResponse<MarketingSourceResponse> search(String query, int page, int size) {
        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by("sortOrder").ascending().and(Sort.by("name").ascending())
        );
        return PageResponse.from(sources.searchActive(query == null ? "" : query.trim(), pageable).map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public List<MarketingSourceResponse> listActive() {
        return sources.findActiveOrdered().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MarketingSourceResponse get(Long id) {
        return toResponse(findActive(id));
    }

    @Transactional
    public MarketingSourceResponse create(MarketingSourceWriteRequest request) {
        NormalizedMarketingSourceInput input = MarketingSourceNormalizer.normalize(request);
        ensureCodeAvailable(input.code(), null);
        return toResponse(sources.save(new LeadSource(input.code(), input.name(), input.sortOrder())));
    }

    @Transactional
    public MarketingSourceResponse update(Long id, MarketingSourceWriteRequest request) {
        LeadSource source = findActive(id);
        NormalizedMarketingSourceInput input = MarketingSourceNormalizer.normalize(request);
        ensureCodeAvailable(input.code(), id);
        source.update(input.code(), input.name(), input.sortOrder());
        return toResponse(source);
    }

    @Transactional
    public void archive(Long id) {
        findActive(id).archive();
    }

    private LeadSource findActive(Long id) {
        return sources.findActiveById(id).orElseThrow(() -> new NotFoundException("Marketing source not found: " + id));
    }

    private void ensureCodeAvailable(String code, Long currentId) {
        sources.findActiveByCode(code).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new InvalidRequestException("Marketing source code already exists: " + code);
            }
        });
    }

    private MarketingSourceResponse toResponse(LeadSource source) {
        return new MarketingSourceResponse(
                source.getId(),
                source.getVersion(),
                source.getCreatedAt(),
                source.getUpdatedAt(),
                source.getCode(),
                source.getName(),
                source.getSortOrder()
        );
    }
}

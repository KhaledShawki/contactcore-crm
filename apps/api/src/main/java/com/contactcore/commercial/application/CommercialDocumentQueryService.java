// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import com.contactcore.commercial.api.CommercialDocumentDetailResponse;
import com.contactcore.commercial.api.CommercialDocumentLineResponse;
import com.contactcore.commercial.api.CommercialDocumentSummaryResponse;
import com.contactcore.commercial.domain.CommercialDocumentLineRepository;
import com.contactcore.commercial.domain.CommercialDocumentRepository;
import com.contactcore.commercial.domain.CommercialDocumentStatus;
import com.contactcore.commercial.domain.CommercialDocumentType;
import com.contactcore.commercial.domain.CommercialSourceSystem;
import com.contactcore.shared.api.InvalidRequestException;
import com.contactcore.shared.api.NotFoundException;
import com.contactcore.shared.api.PageRequestNormalizer;
import com.contactcore.shared.api.PageResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommercialDocumentQueryService {
    private final CommercialDocumentRepository documents;
    private final CommercialDocumentLineRepository lines;

    public CommercialDocumentQueryService(CommercialDocumentRepository documents, CommercialDocumentLineRepository lines) {
        this.documents = documents;
        this.lines = lines;
    }

    @Transactional(readOnly = true)
    public PageResponse<CommercialDocumentSummaryResponse> search(Long businessPartnerId, CommercialDocumentType type,
                                                                  CommercialDocumentStatus status,
                                                                  CommercialSourceSystem sourceSystem,
                                                                  LocalDate fromDate, LocalDate toDate,
                                                                  String query, int page, int size, String sort) {
        validateDateRange(fromDate, toDate);
        int normalizedPage = PageRequestNormalizer.page(page);
        int normalizedSize = PageRequestNormalizer.size(size);
        String normalizedQuery = PageRequestNormalizer.query(query);
        CommercialDocumentSearchCriteria criteria = new CommercialDocumentSearchCriteria(
                businessPartnerId,
                type,
                status,
                sourceSystem,
                fromDate,
                toDate,
                normalizedQuery
        );
        var request = PageRequest.of(normalizedPage, normalizedSize, CommercialDocumentSort.from(sort).sort());
        return PageResponse.from(documents.findAll(CommercialDocumentSpecifications.matching(criteria), request)
                .map(CommercialMapper::toSummaryResponse));
    }

    @Transactional(readOnly = true)
    public CommercialDocumentDetailResponse get(Long id) {
        return CommercialMapper.toDetailResponse(documents.findActiveDetailById(id)
                .orElseThrow(() -> new NotFoundException("Commercial document not found: " + id)));
    }

    @Transactional(readOnly = true)
    public List<CommercialDocumentLineResponse> lines(Long documentId) {
        documents.findActiveById(documentId)
                .orElseThrow(() -> new NotFoundException("Commercial document not found: " + documentId));
        return lines.findActiveByDocumentId(documentId).stream()
                .map(CommercialMapper::toLineResponse)
                .toList();
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new InvalidRequestException("fromDate must not be after toDate");
        }
    }
}

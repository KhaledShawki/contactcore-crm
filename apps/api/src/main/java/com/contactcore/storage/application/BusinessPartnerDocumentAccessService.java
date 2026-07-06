// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.application;

import com.contactcore.shared.api.NotFoundException;
import com.contactcore.storage.domain.BusinessPartnerDocument;
import com.contactcore.storage.domain.BusinessPartnerDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessPartnerDocumentAccessService {
    private final BusinessPartnerDocumentRepository documents;

    public BusinessPartnerDocumentAccessService(BusinessPartnerDocumentRepository documents) {
        this.documents = documents;
    }

    @Transactional(readOnly = true)
    public Long requireActiveBusinessPartnerId(Long documentId) {
        BusinessPartnerDocument document = documents.findActiveById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found: " + documentId));
        return document.getBusinessPartner().getId();
    }
}

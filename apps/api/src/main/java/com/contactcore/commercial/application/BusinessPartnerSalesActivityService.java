// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import com.contactcore.commercial.api.BusinessPartnerCommercialSummaryResponse;
import com.contactcore.commercial.api.BusinessPartnerSalesActivityResponse;
import com.contactcore.commercial.domain.CommercialDocumentRepository;
import com.contactcore.commercial.domain.CommercialDocumentStatus;
import com.contactcore.commercial.domain.CommercialDocumentType;
import com.contactcore.crm.domain.BusinessPartner;
import com.contactcore.crm.domain.BusinessPartnerRepository;
import com.contactcore.shared.api.NotFoundException;
import com.contactcore.shared.api.PageRequestNormalizer;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessPartnerSalesActivityService {
    private static final List<CommercialDocumentStatus> OPEN_STATUSES = List.of(
            CommercialDocumentStatus.OPEN,
            CommercialDocumentStatus.PARTIALLY_FULFILLED
    );

    private final BusinessPartnerRepository businessPartners;
    private final CommercialDocumentRepository documents;

    public BusinessPartnerSalesActivityService(BusinessPartnerRepository businessPartners, CommercialDocumentRepository documents) {
        this.businessPartners = businessPartners;
        this.documents = documents;
    }

    @Transactional(readOnly = true)
    public BusinessPartnerSalesActivityResponse get(Long businessPartnerId, int size) {
        BusinessPartner partner = businessPartners.findActiveById(businessPartnerId)
                .orElseThrow(() -> new NotFoundException("Business partner not found: " + businessPartnerId));
        int normalizedSize = PageRequestNormalizer.size(size);
        BusinessPartnerCommercialSummaryResponse summary = summary(businessPartnerId);
        var recentDocuments = documents.findRecentActiveByBusinessPartnerId(
                        businessPartnerId,
                        PageRequest.of(0, normalizedSize)
                ).stream()
                .map(CommercialMapper::toSummaryResponse)
                .toList();
        return new BusinessPartnerSalesActivityResponse(
                partner.getId(),
                partner.getCode(),
                partner.getName(),
                summary,
                recentDocuments
        );
    }

    private BusinessPartnerCommercialSummaryResponse summary(Long businessPartnerId) {
        return new BusinessPartnerCommercialSummaryResponse(
                documents.countByBusinessPartner_IdAndArchivedAtIsNull(businessPartnerId),
                documents.countByBusinessPartner_IdAndArchivedAtIsNullAndStatusIn(businessPartnerId, OPEN_STATUSES),
                documents.countByBusinessPartner_IdAndArchivedAtIsNullAndTypeAndStatusIn(
                        businessPartnerId,
                        CommercialDocumentType.SALES_QUOTATION,
                        OPEN_STATUSES
                ),
                documents.countByBusinessPartner_IdAndArchivedAtIsNullAndTypeAndStatusIn(
                        businessPartnerId,
                        CommercialDocumentType.SALES_ORDER,
                        OPEN_STATUSES
                ),
                documents.countByBusinessPartner_IdAndArchivedAtIsNullAndType(
                        businessPartnerId,
                        CommercialDocumentType.DELIVERY_NOTE
                ),
                documents.countByBusinessPartner_IdAndArchivedAtIsNullAndType(
                        businessPartnerId,
                        CommercialDocumentType.CUSTOMER_INVOICE
                ),
                documents.latestDocumentDate(businessPartnerId).orElse(null),
                documents.totalAmountsByCurrency(businessPartnerId).stream()
                        .map(CommercialMapper::toCurrencyTotalResponse)
                        .toList()
        );
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import com.contactcore.commercial.api.CommercialAmountByCurrencyResponse;
import com.contactcore.commercial.api.CommercialDocumentDetailResponse;
import com.contactcore.commercial.api.CommercialDocumentLineResponse;
import com.contactcore.commercial.api.CommercialDocumentSummaryResponse;
import com.contactcore.commercial.api.ItemDetailResponse;
import com.contactcore.commercial.api.ItemSummaryResponse;
import com.contactcore.commercial.domain.CommercialAmountByCurrencyProjection;
import com.contactcore.commercial.domain.CommercialDocument;
import com.contactcore.commercial.domain.CommercialDocumentLine;
import com.contactcore.commercial.domain.Item;
import com.contactcore.crm.domain.BusinessPartner;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

final class CommercialMapper {
    private CommercialMapper() {}

    static CommercialDocumentSummaryResponse toSummaryResponse(CommercialDocument document) {
        BusinessPartner partner = document.getBusinessPartner();
        return new CommercialDocumentSummaryResponse(
                document.getId(),
                document.getVersion(),
                document.getCreatedAt(),
                document.getUpdatedAt(),
                document.getSourceSystem().name(),
                document.getSourceTenantId(),
                document.getExternalId(),
                document.getExternalNumber(),
                document.getType().name(),
                document.getStatus().name(),
                document.getSourceStatus(),
                partner == null ? null : partner.getId(),
                document.getBusinessPartnerExternalId(),
                document.getBusinessPartnerCodeSnapshot(),
                document.getBusinessPartnerNameSnapshot(),
                document.getDocumentDate(),
                document.getDueDate(),
                document.getDeliveryDate(),
                document.getCurrency(),
                document.getSubtotalAmount(),
                document.getDiscountAmount(),
                document.getTaxAmount(),
                document.getTotalAmount(),
                document.getOpenAmount(),
                document.getLastSyncedAt()
        );
    }

    static CommercialDocumentDetailResponse toDetailResponse(CommercialDocument document) {
        BusinessPartner partner = document.getBusinessPartner();
        List<CommercialDocumentLineResponse> lines = document.getLines().stream()
                .filter(line -> !line.isArchived())
                .sorted(Comparator.comparing(CommercialDocumentLine::getLineNumber).thenComparing(CommercialDocumentLine::getId))
                .map(CommercialMapper::toLineResponse)
                .toList();
        return new CommercialDocumentDetailResponse(
                document.getId(),
                document.getVersion(),
                document.getCreatedAt(),
                document.getUpdatedAt(),
                document.getSourceSystem().name(),
                document.getSourceTenantId(),
                document.getExternalId(),
                document.getExternalNumber(),
                document.getType().name(),
                document.getStatus().name(),
                document.getSourceStatus(),
                partner == null ? null : partner.getId(),
                document.getBusinessPartnerExternalId(),
                document.getBusinessPartnerCodeSnapshot(),
                document.getBusinessPartnerNameSnapshot(),
                document.getDocumentDate(),
                document.getDueDate(),
                document.getDeliveryDate(),
                document.getCurrency(),
                document.getSubtotalAmount(),
                document.getDiscountAmount(),
                document.getTaxAmount(),
                document.getTotalAmount(),
                document.getOpenAmount(),
                document.getLastSyncedAt(),
                lines
        );
    }

    static CommercialDocumentLineResponse toLineResponse(CommercialDocumentLine line) {
        Item item = line.getItem();
        return new CommercialDocumentLineResponse(
                line.getId(),
                line.getVersion(),
                line.getSourceLineId(),
                line.getLineNumber(),
                item == null ? null : item.getId(),
                line.getItemExternalId(),
                line.getItemCodeSnapshot(),
                line.getItemNameSnapshot(),
                line.getDescription(),
                line.getQuantity(),
                line.getOpenQuantity(),
                line.getUnitOfMeasure(),
                line.getUnitPrice(),
                line.getDiscountPercent(),
                line.getTaxCodeSnapshot(),
                line.getLineTotal(),
                line.getCurrency(),
                line.getDeliveryDate()
        );
    }

    static ItemSummaryResponse toItemSummaryResponse(Item item) {
        return new ItemSummaryResponse(
                item.getId(),
                item.getVersion(),
                item.getCreatedAt(),
                item.getUpdatedAt(),
                item.getSourceSystem().name(),
                item.getSourceTenantId(),
                item.getExternalId(),
                item.getItemCode(),
                item.getName(),
                item.getItemGroup(),
                item.getUnitOfMeasure(),
                item.isActive(),
                item.getLastSyncedAt()
        );
    }

    static ItemDetailResponse toItemDetailResponse(Item item) {
        return new ItemDetailResponse(
                item.getId(),
                item.getVersion(),
                item.getCreatedAt(),
                item.getUpdatedAt(),
                item.getSourceSystem().name(),
                item.getSourceTenantId(),
                item.getExternalId(),
                item.getItemCode(),
                item.getName(),
                item.getDescription(),
                item.getItemGroup(),
                item.getUnitOfMeasure(),
                item.isActive(),
                item.getLastSyncedAt()
        );
    }

    static CommercialAmountByCurrencyResponse toCurrencyTotalResponse(CommercialAmountByCurrencyProjection projection) {
        return new CommercialAmountByCurrencyResponse(
                projection.getCurrency(),
                projection.getDocumentCount() == null ? 0 : projection.getDocumentCount(),
                projection.getTotalAmount() == null ? BigDecimal.ZERO : projection.getTotalAmount(),
                projection.getOpenAmount() == null ? BigDecimal.ZERO : projection.getOpenAmount()
        );
    }
}

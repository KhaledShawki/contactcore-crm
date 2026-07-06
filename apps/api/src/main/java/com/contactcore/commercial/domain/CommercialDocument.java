// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.domain;

import com.contactcore.crm.domain.BusinessPartner;
import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "commercial_document")
public class CommercialDocument extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "source_system", nullable = false, length = 32)
    private CommercialSourceSystem sourceSystem;

    @Column(name = "source_tenant_id", nullable = false, length = 128)
    private String sourceTenantId;

    @Column(name = "external_id", nullable = false, length = 128)
    private String externalId;

    @Column(name = "external_number", nullable = false, length = 128)
    private String externalNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CommercialDocumentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CommercialDocumentStatus status = CommercialDocumentStatus.UNKNOWN;

    @Column(name = "source_status", length = 128)
    private String sourceStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_partner_id")
    private BusinessPartner businessPartner;

    @Column(name = "business_partner_external_id", length = 128)
    private String businessPartnerExternalId;

    @Column(name = "business_partner_code_snapshot", length = 128)
    private String businessPartnerCodeSnapshot;

    @Column(name = "business_partner_name_snapshot", length = 255)
    private String businessPartnerNameSnapshot;

    @Column(name = "document_date", nullable = false)
    private LocalDate documentDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "subtotal_amount", nullable = false, precision = 19, scale = 6)
    private BigDecimal subtotalAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 6)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 19, scale = 6)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 6)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "open_amount", nullable = false, precision = 19, scale = 6)
    private BigDecimal openAmount = BigDecimal.ZERO;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @OneToMany(mappedBy = "commercialDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommercialDocumentLine> lines = new ArrayList<>();

    protected CommercialDocument() {}

    public CommercialDocument(CommercialSourceSystem sourceSystem, String sourceTenantId, String externalId,
                              String externalNumber, CommercialDocumentType type, LocalDate documentDate,
                              String currency) {
        this.sourceSystem = Objects.requireNonNull(sourceSystem, "sourceSystem must not be null");
        this.sourceTenantId = required(sourceTenantId, "sourceTenantId");
        this.externalId = required(externalId, "externalId");
        this.externalNumber = required(externalNumber, "externalNumber");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.documentDate = Objects.requireNonNull(documentDate, "documentDate must not be null");
        this.currency = normalizeCurrency(currency);
    }

    public CommercialSourceSystem getSourceSystem() { return sourceSystem; }
    public String getSourceTenantId() { return sourceTenantId; }
    public String getExternalId() { return externalId; }
    public String getExternalNumber() { return externalNumber; }
    public CommercialDocumentType getType() { return type; }
    public CommercialDocumentStatus getStatus() { return status; }
    public String getSourceStatus() { return sourceStatus; }
    public BusinessPartner getBusinessPartner() { return businessPartner; }
    public String getBusinessPartnerExternalId() { return businessPartnerExternalId; }
    public String getBusinessPartnerCodeSnapshot() { return businessPartnerCodeSnapshot; }
    public String getBusinessPartnerNameSnapshot() { return businessPartnerNameSnapshot; }
    public LocalDate getDocumentDate() { return documentDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public String getCurrency() { return currency; }
    public BigDecimal getSubtotalAmount() { return subtotalAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getOpenAmount() { return openAmount; }
    public Instant getLastSyncedAt() { return lastSyncedAt; }
    public List<CommercialDocumentLine> getLines() { return lines; }

    public void refreshCore(String externalNumber, LocalDate documentDate, String currency) {
        this.externalNumber = required(externalNumber, "externalNumber");
        this.documentDate = Objects.requireNonNull(documentDate, "documentDate must not be null");
        this.currency = normalizeCurrency(currency);
    }

    public void refreshHeader(CommercialDocumentStatus status, String sourceStatus, BusinessPartner businessPartner,
                              String businessPartnerExternalId, String businessPartnerCodeSnapshot,
                              String businessPartnerNameSnapshot, LocalDate dueDate, LocalDate deliveryDate,
                              BigDecimal subtotalAmount, BigDecimal discountAmount, BigDecimal taxAmount,
                              BigDecimal totalAmount, BigDecimal openAmount, Instant lastSyncedAt) {
        this.status = Objects.requireNonNullElse(status, CommercialDocumentStatus.UNKNOWN);
        this.sourceStatus = blankToNull(sourceStatus);
        this.businessPartner = businessPartner;
        this.businessPartnerExternalId = blankToNull(businessPartnerExternalId);
        this.businessPartnerCodeSnapshot = blankToNull(businessPartnerCodeSnapshot);
        this.businessPartnerNameSnapshot = blankToNull(businessPartnerNameSnapshot);
        this.dueDate = dueDate;
        this.deliveryDate = deliveryDate;
        this.subtotalAmount = nonNegative(subtotalAmount, "subtotalAmount");
        this.discountAmount = nonNegative(discountAmount, "discountAmount");
        this.taxAmount = nonNegative(taxAmount, "taxAmount");
        this.totalAmount = nonNegative(totalAmount, "totalAmount");
        this.openAmount = nonNegative(openAmount, "openAmount");
        this.lastSyncedAt = lastSyncedAt;
    }

    public void addLine(CommercialDocumentLine line) {
        lines.add(Objects.requireNonNull(line, "line must not be null"));
        line.attachTo(this);
    }

    public void replaceLines(List<CommercialDocumentLine> replacementLines) {
        lines.clear();
        if (replacementLines == null) {
            return;
        }
        replacementLines.forEach(this::addLine);
    }

    private static String required(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static String normalizeCurrency(String currency) {
        String normalized = required(currency, "currency").toUpperCase(Locale.ROOT);
        if (normalized.length() != 3) {
            throw new IllegalArgumentException("currency must be an ISO 4217 code");
        }
        return normalized;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static BigDecimal nonNegative(BigDecimal value, String fieldName) {
        BigDecimal safeValue = value == null ? BigDecimal.ZERO : value;
        if (safeValue.signum() < 0) {
            throw new IllegalArgumentException(fieldName + " must not be negative");
        }
        return safeValue;
    }
}

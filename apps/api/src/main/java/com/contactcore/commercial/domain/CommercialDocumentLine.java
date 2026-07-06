// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "commercial_document_line")
public class CommercialDocumentLine extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commercial_document_id", nullable = false)
    private CommercialDocument commercialDocument;

    @Column(name = "source_line_id", nullable = false, length = 128)
    private String sourceLineId;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "item_external_id", length = 128)
    private String itemExternalId;

    @Column(name = "item_code_snapshot", length = 128)
    private String itemCodeSnapshot;

    @Column(name = "item_name_snapshot", length = 255)
    private String itemNameSnapshot;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "open_quantity", nullable = false, precision = 19, scale = 6)
    private BigDecimal openQuantity = BigDecimal.ZERO;

    @Column(name = "unit_of_measure", length = 64)
    private String unitOfMeasure;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 6)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "discount_percent", nullable = false, precision = 9, scale = 6)
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(name = "tax_code_snapshot", length = 64)
    private String taxCodeSnapshot;

    @Column(name = "line_total", nullable = false, precision = 19, scale = 6)
    private BigDecimal lineTotal = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    protected CommercialDocumentLine() {}

    public CommercialDocumentLine(String sourceLineId, Integer lineNumber, String currency) {
        this.sourceLineId = required(sourceLineId, "sourceLineId");
        this.lineNumber = normalizeLineNumber(lineNumber);
        this.currency = normalizeCurrency(currency);
    }

    public CommercialDocument getCommercialDocument() { return commercialDocument; }
    public String getSourceLineId() { return sourceLineId; }
    public Integer getLineNumber() { return lineNumber; }
    public Item getItem() { return item; }
    public String getItemExternalId() { return itemExternalId; }
    public String getItemCodeSnapshot() { return itemCodeSnapshot; }
    public String getItemNameSnapshot() { return itemNameSnapshot; }
    public String getDescription() { return description; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getOpenQuantity() { return openQuantity; }
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getDiscountPercent() { return discountPercent; }
    public String getTaxCodeSnapshot() { return taxCodeSnapshot; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public String getCurrency() { return currency; }
    public LocalDate getDeliveryDate() { return deliveryDate; }

    void attachTo(CommercialDocument commercialDocument) {
        this.commercialDocument = Objects.requireNonNull(commercialDocument, "commercialDocument must not be null");
    }

    public void refreshIdentity(Integer lineNumber, String currency) {
        this.lineNumber = normalizeLineNumber(lineNumber);
        this.currency = normalizeCurrency(currency);
    }

    public void refreshLine(Item item, String itemExternalId, String itemCodeSnapshot, String itemNameSnapshot,
                            String description, BigDecimal quantity, BigDecimal openQuantity, String unitOfMeasure,
                            BigDecimal unitPrice, BigDecimal discountPercent, String taxCodeSnapshot,
                            BigDecimal lineTotal, LocalDate deliveryDate) {
        this.item = item;
        this.itemExternalId = blankToNull(itemExternalId);
        this.itemCodeSnapshot = blankToNull(itemCodeSnapshot);
        this.itemNameSnapshot = blankToNull(itemNameSnapshot);
        this.description = blankToNull(description);
        this.quantity = nonNegative(quantity, "quantity");
        this.openQuantity = nonNegative(openQuantity, "openQuantity");
        this.unitOfMeasure = blankToNull(unitOfMeasure);
        this.unitPrice = nonNegative(unitPrice, "unitPrice");
        this.discountPercent = nonNegative(discountPercent, "discountPercent");
        this.taxCodeSnapshot = blankToNull(taxCodeSnapshot);
        this.lineTotal = nonNegative(lineTotal, "lineTotal");
        this.deliveryDate = deliveryDate;
    }

    private static String required(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static Integer normalizeLineNumber(Integer lineNumber) {
        Integer normalized = Objects.requireNonNull(lineNumber, "lineNumber must not be null");
        if (normalized < 0) {
            throw new IllegalArgumentException("lineNumber must not be negative");
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

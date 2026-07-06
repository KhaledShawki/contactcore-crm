// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "commercial_item")
public class Item extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "source_system", nullable = false, length = 32)
    private CommercialSourceSystem sourceSystem;

    @Column(name = "source_tenant_id", nullable = false, length = 128)
    private String sourceTenantId;

    @Column(name = "external_id", nullable = false, length = 128)
    private String externalId;

    @Column(name = "item_code", nullable = false, length = 128)
    private String itemCode;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "item_group", length = 128)
    private String itemGroup;

    @Column(name = "unit_of_measure", length = 64)
    private String unitOfMeasure;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    protected Item() {}

    public Item(CommercialSourceSystem sourceSystem, String sourceTenantId, String externalId, String itemCode, String name) {
        this.sourceSystem = Objects.requireNonNull(sourceSystem, "sourceSystem must not be null");
        this.sourceTenantId = required(sourceTenantId, "sourceTenantId");
        this.externalId = required(externalId, "externalId");
        this.itemCode = required(itemCode, "itemCode");
        this.name = required(name, "name");
    }

    public CommercialSourceSystem getSourceSystem() {
        return sourceSystem;
    }

    public String getSourceTenantId() {
        return sourceTenantId;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void refreshCore(String itemCode, String name) {
        this.itemCode = required(itemCode, "itemCode");
        this.name = required(name, "name");
    }

    public void refreshDetails(String description, String itemGroup, String unitOfMeasure, boolean active, Instant lastSyncedAt) {
        this.description = blankToNull(description);
        this.itemGroup = blankToNull(itemGroup);
        this.unitOfMeasure = blankToNull(unitOfMeasure);
        this.active = active;
        this.lastSyncedAt = lastSyncedAt;
    }

    public void refreshSourceIdentity(CommercialSourceSystem sourceSystem, String sourceTenantId, String externalId) {
        this.sourceSystem = Objects.requireNonNull(sourceSystem, "sourceSystem must not be null");
        this.sourceTenantId = required(sourceTenantId, "sourceTenantId");
        this.externalId = required(externalId, "externalId");
    }

    private static String required(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

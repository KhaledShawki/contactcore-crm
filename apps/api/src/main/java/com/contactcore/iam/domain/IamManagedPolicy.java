// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "iam_managed_policy")
public class IamManagedPolicy extends BaseEntity {
    @Column(nullable = false, unique = true, length = 120)
    private String code;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "system_managed", nullable = false)
    private boolean systemManaged;

    @Column(nullable = false)
    private boolean active = true;

    protected IamManagedPolicy() {}

    public IamManagedPolicy(String code, String name, String description, boolean systemManaged) {
        this.code = normalizeCode(code);
        this.name = require(name, "name");
        this.description = normalizeNullable(description);
        this.systemManaged = systemManaged;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSystemManaged() {
        return systemManaged;
    }

    public boolean isActive() {
        return active;
    }

    public void refresh(String name, String description, boolean systemManaged) {
        this.name = require(name, "name");
        this.description = normalizeNullable(description);
        this.systemManaged = systemManaged;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    private static String normalizeCode(String value) {
        return require(value, "code").toUpperCase(Locale.ROOT).replace('-', '_');
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

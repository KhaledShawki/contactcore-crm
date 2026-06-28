// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "lead_source")
public class LeadSource extends BaseEntity {
    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private Integer sortOrder = 100;

    protected LeadSource() {}

    public LeadSource(String code, String name, Integer sortOrder) {
        this.code = code;
        this.name = name;
        this.sortOrder = sortOrder == null ? 100 : sortOrder;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void update(String code, String name, Integer sortOrder) {
        this.code = code;
        this.name = name;
        this.sortOrder = sortOrder == null ? 100 : sortOrder;
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "contact_method_type")
public class ContactMethodType extends BaseEntity {
    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    protected ContactMethodType() {}

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

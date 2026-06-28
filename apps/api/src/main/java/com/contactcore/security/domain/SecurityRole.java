// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "security_role")
public class SecurityRole extends BaseEntity {
    @Column(nullable = false, unique = true, length = 80)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    protected SecurityRole() {}

    public SecurityRole(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

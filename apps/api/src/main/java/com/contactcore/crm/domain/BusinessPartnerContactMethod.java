// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "business_partner_contact_method")
public class BusinessPartnerContactMethod extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_partner_id", nullable = false)
    private BusinessPartner businessPartner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_method_type_id", nullable = false)
    private ContactMethodType type;

    @Column(length = 80)
    private String label;

    @Column(nullable = false, length = 255)
    private String value;

    @Column(name = "primary_contact", nullable = false)
    private boolean primaryContact;

    protected BusinessPartnerContactMethod() {}

    public BusinessPartnerContactMethod(ContactMethodType type, String label, String value, boolean primaryContact) {
        this.type = type;
        this.label = label;
        this.value = value;
        this.primaryContact = primaryContact;
    }

    public ContactMethodType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public boolean isPrimaryContact() {
        return primaryContact;
    }

    void attachTo(BusinessPartner businessPartner) {
        this.businessPartner = businessPartner;
    }

    public void updateValue(String value) {
        this.value = value;
    }
}

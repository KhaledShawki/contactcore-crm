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
@Table(name = "business_partner_address")
public class BusinessPartnerAddress extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_partner_id", nullable = false)
    private BusinessPartner businessPartner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "address_type", nullable = false, length = 32)
    private String addressType = "PRIMARY";

    @Column(name = "primary_address", nullable = false)
    private boolean primaryAddress = true;

    protected BusinessPartnerAddress() {}

    public BusinessPartnerAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isPrimaryAddress() {
        return primaryAddress;
    }

    void attachTo(BusinessPartner businessPartner) {
        this.businessPartner = businessPartner;
    }
}

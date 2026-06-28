// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "business_partner")
public class BusinessPartner extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kind_id", nullable = false)
    private BusinessPartnerKindRef kind;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private BusinessPartnerStatusRef status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_source_id")
    private LeadSource leadSource;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "text")
    private String notes;

    @OneToMany(mappedBy = "businessPartner", cascade = CascadeType.ALL)
    private List<BusinessPartnerContactMethod> contactMethods = new ArrayList<>();

    @OneToMany(mappedBy = "businessPartner", cascade = CascadeType.ALL)
    private List<BusinessPartnerAddress> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "businessPartner", cascade = CascadeType.ALL)
    private List<BusinessPartnerContactPerson> contactPersons = new ArrayList<>();

    protected BusinessPartner() {}

    public BusinessPartner(BusinessPartnerKindRef kind, BusinessPartnerStatusRef status, String code, String name) {
        this.kind = kind;
        this.status = status;
        this.code = code;
        this.name = name;
    }

    public BusinessPartnerKindRef getKind() {
        return kind;
    }

    public BusinessPartnerStatusRef getStatus() {
        return status;
    }

    public LeadSource getLeadSource() {
        return leadSource;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public List<BusinessPartnerContactMethod> getContactMethods() {
        return contactMethods;
    }

    public List<BusinessPartnerAddress> getAddresses() {
        return addresses;
    }

    public List<BusinessPartnerContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void updateCore(BusinessPartnerKindRef kind, BusinessPartnerStatusRef status, LeadSource leadSource,
                           String code, String name, String notes) {
        this.kind = kind;
        this.status = status;
        this.leadSource = leadSource;
        this.code = code;
        this.name = name;
        this.notes = notes;
    }

    public Optional<BusinessPartnerContactMethod> activePrimaryContact(String typeCode) {
        return contactMethods.stream()
                .filter(method -> !method.isArchived())
                .filter(BusinessPartnerContactMethod::isPrimaryContact)
                .filter(method -> method.getType().getCode().equalsIgnoreCase(typeCode))
                .findFirst();
    }

    public Optional<BusinessPartnerAddress> activePrimaryAddress() {
        return addresses.stream()
                .filter(address -> !address.isArchived())
                .filter(BusinessPartnerAddress::isPrimaryAddress)
                .findFirst();
    }

    public void addContactMethod(BusinessPartnerContactMethod method) {
        contactMethods.add(method);
        method.attachTo(this);
    }

    public void addAddress(BusinessPartnerAddress address) {
        addresses.add(address);
        address.attachTo(this);
    }

    public void addContactPerson(BusinessPartnerContactPerson contactPerson) {
        contactPersons.add(contactPerson);
        contactPerson.attachTo(this);
    }
}

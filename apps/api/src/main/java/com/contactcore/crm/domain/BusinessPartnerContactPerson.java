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
@Table(name = "business_partner_contact_person")
public class BusinessPartnerContactPerson extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_partner_id", nullable = false)
    private BusinessPartner businessPartner;

    @Column(name = "first_name", nullable = false, length = 120)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 120)
    private String lastName;

    @Column(name = "role_title", length = 160)
    private String roleTitle;

    @Column(length = 255)
    private String email;

    @Column(length = 64)
    private String phone;

    @Column(length = 64)
    private String mobile;

    @Column(length = 120)
    private String department;

    @Column(name = "primary_contact", nullable = false)
    private boolean primaryContact;

    @Column(columnDefinition = "text")
    private String notes;

    protected BusinessPartnerContactPerson() {}

    public BusinessPartnerContactPerson(String firstName, String lastName, String roleTitle, String email,
                                        String phone, String mobile, String department, boolean primaryContact,
                                        String notes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.roleTitle = roleTitle;
        this.email = email;
        this.phone = phone;
        this.mobile = mobile;
        this.department = department;
        this.primaryContact = primaryContact;
        this.notes = notes;
    }

    public BusinessPartner getBusinessPartner() {
        return businessPartner;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getMobile() {
        return mobile;
    }

    public String getDepartment() {
        return department;
    }

    public boolean isPrimaryContact() {
        return primaryContact;
    }

    public String getNotes() {
        return notes;
    }

    void attachTo(BusinessPartner businessPartner) {
        this.businessPartner = businessPartner;
    }

    public void update(String firstName, String lastName, String roleTitle, String email, String phone,
                       String mobile, String department, boolean primaryContact, String notes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.roleTitle = roleTitle;
        this.email = email;
        this.phone = phone;
        this.mobile = mobile;
        this.department = department;
        this.primaryContact = primaryContact;
        this.notes = notes;
    }

    public void setPrimaryContact(boolean primaryContact) {
        this.primaryContact = primaryContact;
    }

    public String displayName() {
        return firstName + " " + lastName;
    }
}

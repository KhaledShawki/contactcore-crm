// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.domain;

import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "address")
public class Address extends BaseEntity {
    @Column(length = 255)
    private String line1;

    @Column(length = 255)
    private String line2;

    @Column(length = 128)
    private String city;

    @Column(name = "postal_code", length = 64)
    private String postalCode;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    protected Address() {}

    public Address(String line1, String line2, String city, String postalCode, String countryCode) {
        update(line1, line2, city, postalCode, countryCode);
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void update(String line1, String line2, String city, String postalCode, String countryCode) {
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
    }
}

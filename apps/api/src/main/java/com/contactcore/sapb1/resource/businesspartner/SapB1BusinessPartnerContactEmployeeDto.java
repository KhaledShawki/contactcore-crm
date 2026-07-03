// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.businesspartner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SapB1BusinessPartnerContactEmployeeDto(
        @JsonProperty("InternalCode") Integer internalCode,
        @JsonProperty("Name") String name,
        @JsonProperty("FirstName") String firstName,
        @JsonProperty("MiddleName") String middleName,
        @JsonProperty("LastName") String lastName,
        @JsonProperty("Title") String title,
        @JsonProperty("Position") String position,
        @JsonProperty("E_Mail") String email,
        @JsonProperty("Phone1") String phone,
        @JsonProperty("MobilePhone") String mobilePhone,
        @JsonProperty("Active") String active
) {}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.businesspartner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SapB1BusinessPartnerAddressDto(
        @JsonProperty("AddressName") String addressName,
        @JsonProperty("AddressType") String addressType,
        @JsonProperty("Street") String street,
        @JsonProperty("Block") String block,
        @JsonProperty("ZipCode") String zipCode,
        @JsonProperty("City") String city,
        @JsonProperty("County") String county,
        @JsonProperty("State") String state,
        @JsonProperty("Country") String country,
        @JsonProperty("BPCode") String bpCode,
        @JsonProperty("RowNum") Integer rowNum
) {}

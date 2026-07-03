// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.businesspartner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SapB1BusinessPartnerDto(
        @JsonProperty("CardCode") String cardCode,
        @JsonProperty("CardName") String cardName,
        @JsonProperty("CardType") String cardType,
        @JsonProperty("Phone1") String phone1,
        @JsonProperty("Phone2") String phone2,
        @JsonProperty("Cellular") String cellular,
        @JsonProperty("EmailAddress") String emailAddress,
        @JsonProperty("Website") String website,
        @JsonProperty("Currency") String currency,
        @JsonProperty("CurrentAccountBalance") BigDecimal currentAccountBalance,
        @JsonProperty("CreditLimit") BigDecimal creditLimit,
        @JsonProperty("MaxCommitment") BigDecimal maxCommitment,
        @JsonProperty("FederalTaxID") String federalTaxId,
        @JsonProperty("VatLiable") String vatLiable,
        @JsonProperty("PayTermsGrpCode") String paymentTermsGroupCode,
        @JsonProperty("Valid") String valid,
        @JsonProperty("Frozen") String frozen,
        @JsonProperty("DefaultBillingAddress") String defaultBillingAddress,
        @JsonProperty("DefaultShippingAddress") String defaultShippingAddress,
        @JsonProperty("ContactPerson") String defaultContactPerson,
        @JsonProperty("SalesPersonCode") Integer salesPersonCode,
        @JsonProperty("GroupCode") Integer groupCode,
        @JsonProperty("Territory") Integer territory,
        @JsonProperty("BPAddresses") List<SapB1BusinessPartnerAddressDto> addresses,
        @JsonProperty("ContactEmployees") List<SapB1BusinessPartnerContactEmployeeDto> contactEmployees
) {
    public SapB1BusinessPartnerDto {
        addresses = addresses == null ? List.of() : List.copyOf(addresses);
        contactEmployees = contactEmployees == null ? List.of() : List.copyOf(contactEmployees);
    }
}

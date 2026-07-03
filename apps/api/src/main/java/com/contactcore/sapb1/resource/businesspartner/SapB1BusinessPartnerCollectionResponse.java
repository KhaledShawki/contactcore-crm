// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.businesspartner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SapB1BusinessPartnerCollectionResponse(
        @JsonProperty("value") List<SapB1BusinessPartnerDto> value
) {
    public SapB1BusinessPartnerCollectionResponse {
        value = value == null ? List.of() : List.copyOf(value);
    }
}

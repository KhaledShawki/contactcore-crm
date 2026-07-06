// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SapB1DashboardDocumentCollectionResponse(
        @JsonProperty("value") List<SapB1DashboardDocumentDto> value
) {
    public SapB1DashboardDocumentCollectionResponse {
        value = value == null ? List.of() : List.copyOf(value);
    }
}

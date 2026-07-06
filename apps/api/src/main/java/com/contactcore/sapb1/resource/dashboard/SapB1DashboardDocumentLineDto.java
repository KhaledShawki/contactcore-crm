// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SapB1DashboardDocumentLineDto(
        @JsonProperty("ItemCode") String itemCode,
        @JsonProperty("ItemDescription") String itemDescription,
        @JsonProperty("Quantity") BigDecimal quantity,
        @JsonProperty("LineTotal") BigDecimal lineTotal
) {}

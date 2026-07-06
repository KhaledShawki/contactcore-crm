// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.resource.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SapB1DashboardDocumentDto(
        @JsonProperty("DocEntry") Integer docEntry,
        @JsonProperty("DocNum") Integer docNum,
        @JsonProperty("CardCode") String cardCode,
        @JsonProperty("CardName") String cardName,
        @JsonProperty("DocDate") LocalDate docDate,
        @JsonProperty("DocDueDate") LocalDate docDueDate,
        @JsonProperty("DocTotal") BigDecimal docTotal,
        @JsonProperty("PaidToDate") BigDecimal paidToDate,
        @JsonProperty("DocCurrency") String docCurrency,
        @JsonProperty("DocumentStatus") String documentStatus,
        @JsonProperty("Cancelled") String cancelled,
        @JsonProperty("DocumentLines") List<SapB1DashboardDocumentLineDto> documentLines
) {
    public SapB1DashboardDocumentDto {
        documentLines = documentLines == null ? List.of() : List.copyOf(documentLines);
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

import java.util.List;

public record CommercialDashboardSnapshot(
        List<CommercialDashboardDocument> documents
) {
    public CommercialDashboardSnapshot {
        documents = documents == null ? List.of() : List.copyOf(documents);
    }

    public List<CommercialDashboardDocument> invoices() {
        return documents.stream()
                .filter(document -> document.type() == CommercialDashboardDocumentType.INVOICE)
                .toList();
    }
}

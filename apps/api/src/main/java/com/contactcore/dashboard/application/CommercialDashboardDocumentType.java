// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.application;

public enum CommercialDashboardDocumentType {
    QUOTATION("Quotations"),
    SALES_ORDER("Sales orders"),
    DELIVERY_NOTE("Delivery notes"),
    INVOICE("Invoices"),
    CREDIT_NOTE("Credit notes");

    private final String label;

    CommercialDashboardDocumentType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

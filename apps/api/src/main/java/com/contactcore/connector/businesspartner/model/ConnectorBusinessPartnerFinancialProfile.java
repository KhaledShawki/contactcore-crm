// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.model;

import java.math.BigDecimal;

public record ConnectorBusinessPartnerFinancialProfile(
        String currency,
        BigDecimal currentBalance,
        BigDecimal creditLimit,
        BigDecimal openOrdersBalance,
        BigDecimal openDeliveryBalance,
        BigDecimal openInvoiceBalance,
        BigDecimal overdueBalance,
        BigDecimal availableCredit,
        String paymentTermsCode,
        String paymentTermsName,
        String taxId,
        String vatGroup,
        String accountReceivableCode,
        String accountPayableCode
) {
    public ConnectorBusinessPartnerFinancialProfile {
        currency = blankToNull(currency);
        paymentTermsCode = blankToNull(paymentTermsCode);
        paymentTermsName = blankToNull(paymentTermsName);
        taxId = blankToNull(taxId);
        vatGroup = blankToNull(vatGroup);
        accountReceivableCode = blankToNull(accountReceivableCode);
        accountPayableCode = blankToNull(accountPayableCode);
    }

    public static ConnectorBusinessPartnerFinancialProfile empty() {
        return new ConnectorBusinessPartnerFinancialProfile(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

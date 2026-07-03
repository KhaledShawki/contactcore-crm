// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.connector.businesspartner.query;

public enum ConnectorBusinessPartnerSort {
    CODE_ASC,
    CODE_DESC,
    NAME_ASC,
    NAME_DESC;

    public static ConnectorBusinessPartnerSort from(String value) {
        if (value == null || value.isBlank()) {
            return CODE_ASC;
        }
        return switch (value.trim().toLowerCase()) {
            case "code_desc" -> CODE_DESC;
            case "name_asc" -> NAME_ASC;
            case "name_desc" -> NAME_DESC;
            default -> CODE_ASC;
        };
    }

    public String apiValue() {
        return switch (this) {
            case CODE_DESC -> "code_desc";
            case NAME_ASC -> "name_asc";
            case NAME_DESC -> "name_desc";
            case CODE_ASC -> "code_asc";
        };
    }
}

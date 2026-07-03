// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.sapb1.client;

public class SapB1SessionExpiredException extends SapB1ServiceLayerException {
    public SapB1SessionExpiredException() {
        super("SAP B1 session expired. Reconnect to the selected CRM connector.");
    }
}

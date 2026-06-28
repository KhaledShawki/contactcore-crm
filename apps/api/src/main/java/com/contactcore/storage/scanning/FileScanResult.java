// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.scanning;

public record FileScanResult(
        FileScanStatus status,
        String scannerMessage
) {
    public static FileScanResult clean(String scannerMessage) {
        return new FileScanResult(FileScanStatus.CLEAN, scannerMessage);
    }

    public static FileScanResult infected(String scannerMessage) {
        return new FileScanResult(FileScanStatus.INFECTED, scannerMessage);
    }

    public boolean clean() {
        return status == FileScanStatus.CLEAN;
    }
}

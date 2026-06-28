// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.scanning;

public interface FileScanner {
    FileScanResult scan(String filename, byte[] content);
}

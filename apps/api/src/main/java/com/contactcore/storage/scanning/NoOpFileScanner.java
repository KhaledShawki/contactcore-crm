// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.scanning;

public class NoOpFileScanner implements FileScanner {
    @Override
    public FileScanResult scan(String filename, byte[] content) {
        return FileScanResult.clean("File scanning is disabled.");
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.application;

public record ReportFile(
        String filename,
        String contentType,
        byte[] content
) {}

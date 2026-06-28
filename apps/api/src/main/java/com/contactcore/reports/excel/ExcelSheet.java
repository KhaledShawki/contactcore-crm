// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.excel;

import java.util.List;

public record ExcelSheet<T>(
        String name,
        List<ExcelColumn<T>> columns,
        List<T> rows
) {}

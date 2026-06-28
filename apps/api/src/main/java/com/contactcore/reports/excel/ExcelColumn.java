// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.excel;

import java.util.function.Function;

public record ExcelColumn<T>(
        String header,
        int width,
        ExcelCellType type,
        Function<T, Object> valueExtractor
) {
    public static <T> ExcelColumn<T> text(String header, int width, Function<T, Object> valueExtractor) {
        return new ExcelColumn<>(header, width, ExcelCellType.TEXT, valueExtractor);
    }

    public static <T> ExcelColumn<T> integer(String header, int width, Function<T, Object> valueExtractor) {
        return new ExcelColumn<>(header, width, ExcelCellType.INTEGER, valueExtractor);
    }

    public static <T> ExcelColumn<T> decimal(String header, int width, Function<T, Object> valueExtractor) {
        return new ExcelColumn<>(header, width, ExcelCellType.DECIMAL, valueExtractor);
    }

    public static <T> ExcelColumn<T> instant(String header, int width, Function<T, Object> valueExtractor) {
        return new ExcelColumn<>(header, width, ExcelCellType.INSTANT, valueExtractor);
    }
}

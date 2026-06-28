// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.excel;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class ExcelWorkbookWriterTest {
    private final ExcelWorkbookWriter writer = new ExcelWorkbookWriter();

    @Test
    void writesHeadersDataAndEscapesFormulaLikeText() throws Exception {
        byte[] workbookBytes = writer.write(new ExcelSheet<>(
                "Customers",
                List.of(
                        ExcelColumn.text("Name", 24, TestRow::name),
                        ExcelColumn.integer("Count", 12, TestRow::count),
                        ExcelColumn.instant("Created at", 20, TestRow::createdAt)
                ),
                List.of(new TestRow("=HYPERLINK(\"https://evil.example\")", 3, Instant.parse("2026-06-26T08:00:00Z")))
        ));

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(workbookBytes))) {
            var sheet = workbook.getSheet("Customers");
            assertThat(sheet).isNotNull();
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Name");
            assertThat(sheet.getRow(1).getCell(0).getStringCellValue()).startsWith("'=");
            assertThat(sheet.getRow(1).getCell(1).getNumericCellValue()).isEqualTo(3.0);
            assertThat(sheet.getPaneInformation()).isNotNull();
        }
    }

    @Test
    void writesLargeStreamingWorkbookWithoutFlushingHeaderBeforeFilterIsConfigured() throws Exception {
        List<TestRow> rows = new ArrayList<>();
        for (int index = 0; index < 250; index++) {
            rows.add(new TestRow("Customer " + index, index, Instant.parse("2026-06-26T08:00:00Z")));
        }

        byte[] workbookBytes = writer.write(new ExcelSheet<>(
                "Customers",
                List.of(
                        ExcelColumn.text("Name", 24, TestRow::name),
                        ExcelColumn.integer("Count", 12, TestRow::count),
                        ExcelColumn.instant("Created at", 20, TestRow::createdAt)
                ),
                rows
        ));

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(workbookBytes))) {
            var sheet = workbook.getSheet("Customers");
            assertThat(sheet).isNotNull();
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Name");
            assertThat(sheet.getRow(250).getCell(0).getStringCellValue()).isEqualTo("Customer 249");
        }
    }

    private record TestRow(String name, Integer count, Instant createdAt) {}
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ExcelWorkbookWriter {
    public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final int STREAMING_WINDOW_SIZE = 100;
    private static final int MAX_SHEET_NAME_LENGTH = 31;

    public <T> byte[] write(ExcelSheet<T> sheet) {
        return write(List.of(sheet));
    }

    public byte[] write(List<? extends ExcelSheet<?>> sheets) {
        if (sheets == null || sheets.isEmpty()) {
            throw new IllegalArgumentException("At least one Excel sheet is required.");
        }
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(STREAMING_WINDOW_SIZE); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            workbook.setCompressTempFiles(true);
            WorkbookStyles styles = WorkbookStyles.create(workbook);
            for (ExcelSheet<?> sheet : sheets) {
                writeSheet(workbook, styles, sheet);
            }
            workbook.write(output);
            workbook.dispose();
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not generate Excel workbook.", exception);
        }
    }

    private <T> void writeSheet(SXSSFWorkbook workbook, WorkbookStyles styles, ExcelSheet<T> sheetDefinition) {
        if (sheetDefinition.columns().isEmpty()) {
            throw new IllegalArgumentException("Excel sheet must contain at least one column.");
        }
        SXSSFSheet sheet = workbook.createSheet(safeSheetName(sheetDefinition.name()));
        sheet.setRandomAccessWindowSize(STREAMING_WINDOW_SIZE);
        sheet.createFreezePane(0, 1);
        writeHeader(sheet, styles.header(), sheetDefinition.columns());
        applyAutoFilter(sheet, sheetDefinition.columns().size(), sheetDefinition.rows().size());
        writeRows(sheet, styles, sheetDefinition.columns(), sheetDefinition.rows());
        applyColumnWidths(sheet, sheetDefinition.columns());
    }


    private void applyAutoFilter(SXSSFSheet sheet, int columnCount, int rowCount) {
        // SXSSF flushes older rows while streaming large workbooks. Configure the
        // filter range while the header row is still available; otherwise exports
        // with more rows than the streaming window can fail after row 0 is flushed.
        sheet.setAutoFilter(new CellRangeAddress(0, Math.max(rowCount, 1), 0, columnCount - 1));
    }

    private <T> void writeHeader(SXSSFSheet sheet, CellStyle headerStyle, List<ExcelColumn<T>> columns) {
        Row row = sheet.createRow(0);
        row.setHeightInPoints(22);
        for (int index = 0; index < columns.size(); index++) {
            Cell cell = row.createCell(index, CellType.STRING);
            cell.setCellValue(columns.get(index).header());
            cell.setCellStyle(headerStyle);
        }
    }

    private <T> void writeRows(SXSSFSheet sheet, WorkbookStyles styles, List<ExcelColumn<T>> columns, List<T> rows) {
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1);
            row.setHeightInPoints(19);
            T source = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                ExcelColumn<T> column = columns.get(columnIndex);
                writeCell(row.createCell(columnIndex), styles, column, column.valueExtractor().apply(source));
            }
        }
    }

    private <T> void writeCell(Cell cell, WorkbookStyles styles, ExcelColumn<T> column, Object value) {
        if (value == null) {
            cell.setBlank();
            cell.setCellStyle(styles.text());
            return;
        }
        switch (column.type()) {
            case INTEGER -> writeNumber(cell, styles.integer(), value);
            case DECIMAL -> writeNumber(cell, styles.decimal(), value);
            case INSTANT -> writeInstant(cell, styles.instant(), value);
            case TEXT -> writeText(cell, styles.text(), value);
        }
    }

    private void writeText(Cell cell, CellStyle style, Object value) {
        cell.setCellValue(safeText(String.valueOf(value)));
        cell.setCellStyle(style);
    }

    private void writeNumber(Cell cell, CellStyle style, Object value) {
        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else if (value instanceof BigDecimal decimal) {
            cell.setCellValue(decimal.doubleValue());
        } else {
            cell.setCellValue(safeText(String.valueOf(value)));
        }
        cell.setCellStyle(style);
    }

    private void writeInstant(Cell cell, CellStyle style, Object value) {
        if (value instanceof Instant instant) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            cell.setCellValue(Date.from(localDateTime.toInstant(ZoneOffset.UTC)));
            cell.setCellStyle(style);
            return;
        }
        cell.setCellValue(safeText(String.valueOf(value)));
        cell.setCellStyle(style);
    }

    private String safeText(String value) {
        String trimmed = value == null ? "" : value.replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", "");
        if (trimmed.startsWith("=") || trimmed.startsWith("+") || trimmed.startsWith("-") || trimmed.startsWith("@")) {
            return "'" + trimmed;
        }
        return trimmed;
    }

    private String safeSheetName(String value) {
        String normalized = value == null || value.isBlank() ? "Report" : value.trim();
        normalized = normalized.replaceAll("[\\[\\]\\*\\?/\\\\:]", " ").trim();
        if (normalized.isBlank()) {
            return "Report";
        }
        return normalized.length() > MAX_SHEET_NAME_LENGTH ? normalized.substring(0, MAX_SHEET_NAME_LENGTH) : normalized;
    }

    private <T> void applyColumnWidths(SXSSFSheet sheet, List<ExcelColumn<T>> columns) {
        for (int index = 0; index < columns.size(); index++) {
            int width = Math.max(8, Math.min(columns.get(index).width(), 44));
            sheet.setColumnWidth(index, width * 256);
        }
    }

    private record WorkbookStyles(CellStyle header, CellStyle text, CellStyle integer, CellStyle decimal, CellStyle instant) {
        static WorkbookStyles create(SXSSFWorkbook workbook) {
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle header = workbook.createCellStyle();
            header.setFont(headerFont);
            header.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            header.setAlignment(HorizontalAlignment.LEFT);
            header.setVerticalAlignment(VerticalAlignment.CENTER);
            header.setBorderBottom(BorderStyle.THIN);

            CellStyle text = baseStyle(workbook);
            text.setWrapText(true);

            CellStyle integer = baseStyle(workbook);
            integer.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

            CellStyle decimal = baseStyle(workbook);
            decimal.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            CellStyle instant = baseStyle(workbook);
            instant.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm"));

            return new WorkbookStyles(header, text, integer, decimal, instant);
        }

        private static CellStyle baseStyle(SXSSFWorkbook workbook) {
            CellStyle style = workbook.createCellStyle();
            style.setVerticalAlignment(VerticalAlignment.TOP);
            style.setBorderBottom(BorderStyle.HAIR);
            style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            return style;
        }
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record UiWidget(
        String key,
        String type,
        String title,
        String titleKey,
        String description,
        String descriptionKey,
        UiWidgetDataSource dataSource,
        String dataPath,
        Map<String, String> bindings,
        String format,
        int columns,
        List<UiWidgetTableColumn> tableColumns,
        UiCapabilityReference requiredCapability,
        boolean visible
) {
    public UiWidget {
        key = require(key, "key");
        type = require(type, "type");
        title = require(title, "title");
        titleKey = normalizeOptional(titleKey);
        description = normalizeOptional(description);
        descriptionKey = normalizeOptional(descriptionKey);
        dataPath = normalizeOptional(dataPath);
        bindings = copy(bindings);
        format = normalizeOptional(format);
        if (columns < 1 || columns > 12) {
            throw new IllegalArgumentException("columns must be between 1 and 12");
        }
        tableColumns = tableColumns == null ? List.of() : List.copyOf(tableColumns);
    }

    public static Builder builder(String key, String type, String title) {
        return new Builder(key, type, title);
    }

    private static Map<String, String> copy(Map<String, String> values) {
        if (values == null || values.isEmpty()) {
            return Map.of();
        }
        Map<String, String> copied = new LinkedHashMap<>();
        values.forEach((key, value) -> copied.put(require(key, "binding key"), value == null ? "" : value));
        return Collections.unmodifiableMap(copied);
    }

    private static String require(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }

    public static final class Builder {
        private final String key;
        private final String type;
        private final String title;
        private String titleKey;
        private String description;
        private String descriptionKey;
        private UiWidgetDataSource dataSource;
        private String dataPath;
        private Map<String, String> bindings = Map.of();
        private String format;
        private int columns = 1;
        private List<UiWidgetTableColumn> tableColumns = List.of();
        private UiCapabilityReference requiredCapability;
        private boolean visible = true;

        private Builder(String key, String type, String title) {
            this.key = key;
            this.type = type;
            this.title = title;
        }

        public Builder titleKey(String value) {
            this.titleKey = value;
            return this;
        }

        public Builder description(String value) {
            this.description = value;
            return this;
        }

        public Builder descriptionKey(String value) {
            this.descriptionKey = value;
            return this;
        }

        public Builder dataSource(UiWidgetDataSource value) {
            this.dataSource = value;
            return this;
        }

        public Builder dataPath(String value) {
            this.dataPath = value;
            return this;
        }

        public Builder bindings(Map<String, String> values) {
            this.bindings = values;
            return this;
        }

        public Builder format(String value) {
            this.format = value;
            return this;
        }

        public Builder columns(int value) {
            this.columns = value;
            return this;
        }

        public Builder tableColumns(List<UiWidgetTableColumn> values) {
            this.tableColumns = values;
            return this;
        }

        public Builder requiredCapability(UiCapabilityReference value) {
            this.requiredCapability = value;
            return this;
        }

        public Builder visible(boolean value) {
            this.visible = value;
            return this;
        }

        public UiWidget build() {
            return new UiWidget(key, type, title, titleKey, description, descriptionKey, dataSource, dataPath,
                    bindings, format, columns, tableColumns, requiredCapability, visible);
        }
    }
}

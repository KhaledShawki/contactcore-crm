// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

class SqlTemplateLoaderTest {
    private final SqlTemplateLoader loader = new SqlTemplateLoader(new DefaultResourceLoader());

    @Test
    void loadsSqlTemplateFromClasspath() {
        String sql = loader.load("reports/crm/business-partners.sql");

        assertThat(sql).contains("from business_partner partner");
    }

    @Test
    void rejectsMissingTemplate() {
        assertThatThrownBy(() -> loader.load("reports/missing.sql"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SQL template does not exist");
    }
}

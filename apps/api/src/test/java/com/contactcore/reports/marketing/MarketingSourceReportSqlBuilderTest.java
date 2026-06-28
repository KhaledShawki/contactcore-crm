// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.marketing;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.shared.sql.SqlTemplateLoader;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

class MarketingSourceReportSqlBuilderTest {
    private final MarketingSourceReportSqlBuilder builder = new MarketingSourceReportSqlBuilder(
            new SqlTemplateLoader(new DefaultResourceLoader())
    );

    @Test
    void loadsMarketingSourceReportSqlFromClasspathResource() {
        String sql = builder.build();

        assertThat(sql)
                .contains("source.code as source_code")
                .contains("concat('%', :query, '%')")
                .contains("order by source.sort_order asc, source.name asc, source.id asc");
    }
}

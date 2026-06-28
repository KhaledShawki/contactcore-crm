// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.marketing;

import com.contactcore.shared.sql.SqlTemplateLoader;
import org.springframework.stereotype.Component;

@Component
class MarketingSourceReportSqlBuilder {
    private static final String TEMPLATE_LOCATION = "reports/marketing/marketing-sources.sql";

    private final SqlTemplateLoader templates;

    MarketingSourceReportSqlBuilder(SqlTemplateLoader templates) {
        this.templates = templates;
    }

    String build() {
        return templates.load(TEMPLATE_LOCATION);
    }
}

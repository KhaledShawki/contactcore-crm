// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.crm;

import com.contactcore.shared.sql.SqlTemplateLoader;
import org.springframework.stereotype.Component;

@Component
class BusinessPartnerReportSqlBuilder {
    private static final String TEMPLATE_LOCATION = "reports/crm/business-partners.sql";
    private static final String ORDER_BY_PLACEHOLDER = "/*{{ORDER_BY}}*/";

    private final SqlTemplateLoader templates;

    BusinessPartnerReportSqlBuilder(SqlTemplateLoader templates) {
        this.templates = templates;
    }

    String build(String orderBy) {
        String sql = templates.load(TEMPLATE_LOCATION).replace(ORDER_BY_PLACEHOLDER, orderBy);
        if (sql.contains(ORDER_BY_PLACEHOLDER)) {
            throw new IllegalStateException("Business partner report SQL template is missing order-by replacement.");
        }
        return sql;
    }
}

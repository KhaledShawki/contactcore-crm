// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.reports.crm;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.shared.sql.SqlTemplateLoader;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

class BusinessPartnerReportSqlBuilderTest {
    private final BusinessPartnerReportSqlBuilder builder = new BusinessPartnerReportSqlBuilder(
            new SqlTemplateLoader(new DefaultResourceLoader())
    );

    @Test
    void rendersOrderByWithoutInterpretingSqlWildcardsAsFormatTokens() {
        String sql = builder.build("partner.updated_at desc, partner.id desc");

        assertThat(sql)
                .contains("order by partner.updated_at desc, partner.id desc")
                .contains("concat('%', :query, '%')")
                .doesNotContain("/*{{ORDER_BY}}*/")
                .doesNotContain("order by %s");
    }

    @Test
    void usesUniqueColumnAliasesForHibernateNativeQueryAutoDiscovery() {
        String sql = builder.build("partner.updated_at desc, partner.id desc");

        assertThat(sql)
                .contains("kind.code as partner_type")
                .contains("partner.code as partner_code")
                .contains("partner.name as partner_name")
                .contains("status.name as status_name")
                .contains("partner.created_at as partner_created_at")
                .contains("partner.updated_at as partner_updated_at");
    }
}

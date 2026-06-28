// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.search;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.shared.sql.SqlTemplateLoader;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

class BusinessPartnerSearchSqlBuilderTest {
    private final BusinessPartnerSearchSqlBuilder builder = new BusinessPartnerSearchSqlBuilder(
            new SqlTemplateLoader(new DefaultResourceLoader())
    );

    @Test
    void buildsBlankSearchWithoutDistinctOrHeavySearchPredicate() {
        BusinessPartnerSearchCriteria criteria = new BusinessPartnerSearchCriteria(
                "CUSTOMER",
                "",
                "%%",
                0,
                20,
                0,
                BusinessPartnerSearchSort.UPDATED_DESC
        );

        String sql = builder.searchSql(criteria);

        assertThat(sql).doesNotContain("select distinct");
        assertThat(sql).doesNotContain("/*{{ORDER_BY}}*/");
        assertThat(sql).doesNotContain("/*{{SEARCH_PREDICATE}}*/");
        assertThat(sql).doesNotContain("lower(partner.code) like :queryPattern");
        assertThat(sql).contains("order by partner.updated_at desc, partner.id desc");
    }

    @Test
    void buildsFilteredSearchWithExistsPredicatesAndWhitelistedSort() {
        BusinessPartnerSearchCriteria criteria = new BusinessPartnerSearchCriteria(
                "CUSTOMER",
                "meyer",
                "%meyer%",
                0,
                20,
                0,
                BusinessPartnerSearchSort.STATUS_ASC
        );

        String sql = builder.searchSql(criteria);
        String countSql = builder.countSql(criteria);

        assertThat(sql).doesNotContain("select distinct");
        assertThat(sql).contains("exists (");
        assertThat(sql).contains("from business_partner_contact_method method");
        assertThat(sql).contains("from business_partner_contact_person person");
        assertThat(sql).contains("order by lower(status.name) asc, lower(partner.name) asc, partner.id asc");
        assertThat(countSql).contains("lower(partner.code) like :queryPattern");
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CrmBusinessPartnerSqlBuilderTest {
    private final CrmBusinessPartnerSqlBuilder builder = new CrmBusinessPartnerSqlBuilder();

    @Test
    void buildsSqlWithSafeOrderAndLimitSpacing() {
        String sql = builder.buildSelect("and kind.code = 'LEAD'", "partner.updated_at desc, partner.id desc");

        assertThat(sql).contains("order by partner.updated_at desc, partner.id desc");
        assertThat(sql).contains("limit :limit");
        assertThat(sql).doesNotContain("order bypartner");
        assertThat(sql).doesNotContain("desclimit");
    }

    @Test
    void omitsUnnecessaryContactPersonJoinThatMultipliesPartnerRows() {
        String sql = builder.buildSelect("", "partner.id desc");

        assertThat(sql).doesNotContain("left join business_partner_contact_person person on");
        assertThat(sql).contains("left join business_partner_contact_person primary_person on");
    }
}

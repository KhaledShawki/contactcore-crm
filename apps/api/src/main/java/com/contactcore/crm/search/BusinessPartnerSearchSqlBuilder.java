// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.search;

import com.contactcore.shared.sql.SqlTemplateLoader;
import org.springframework.stereotype.Component;

@Component
class BusinessPartnerSearchSqlBuilder {
    private static final String SEARCH_TEMPLATE_LOCATION = "crm/business-partners/search.sql";
    private static final String COUNT_TEMPLATE_LOCATION = "crm/business-partners/count.sql";
    private static final String SEARCH_PREDICATE_PLACEHOLDER = "/*{{SEARCH_PREDICATE}}*/";
    private static final String ORDER_BY_PLACEHOLDER = "/*{{ORDER_BY}}*/";

    private static final String SEARCH_PREDICATE = """
              and (
                   lower(partner.code) like :queryPattern
                   or lower(partner.name) like :queryPattern
                   or exists (
                       select 1
                       from business_partner_contact_method method
                       where method.business_partner_id = partner.id
                         and method.archived_at is null
                         and lower(method.value) like :queryPattern
                   )
                   or exists (
                       select 1
                       from business_partner_contact_person person
                       where person.business_partner_id = partner.id
                         and person.archived_at is null
                         and (
                              lower(coalesce(person.first_name, '') || ' ' || coalesce(person.last_name, '')) like :queryPattern
                              or lower(coalesce(person.email, '')) like :queryPattern
                              or lower(coalesce(person.phone, '')) like :queryPattern
                              or lower(coalesce(person.mobile, '')) like :queryPattern
                         )
                   )
              )
            """;

    private final SqlTemplateLoader templates;

    BusinessPartnerSearchSqlBuilder(SqlTemplateLoader templates) {
        this.templates = templates;
    }

    String searchSql(BusinessPartnerSearchCriteria criteria) {
        return templates.load(SEARCH_TEMPLATE_LOCATION)
                .replace(SEARCH_PREDICATE_PLACEHOLDER, criteria.hasQuery() ? SEARCH_PREDICATE : "")
                .replace(ORDER_BY_PLACEHOLDER, criteria.sort().orderBy());
    }

    String countSql(BusinessPartnerSearchCriteria criteria) {
        return templates.load(COUNT_TEMPLATE_LOCATION)
                .replace(SEARCH_PREDICATE_PLACEHOLDER, criteria.hasQuery() ? SEARCH_PREDICATE : "");
    }
}

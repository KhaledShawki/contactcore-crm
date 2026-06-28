// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import org.springframework.stereotype.Component;

@Component
public class CrmBusinessPartnerSqlBuilder {
    private static final String DEFAULT_ORDER_BY = "partner.updated_at desc, partner.id desc";

    public String buildSelect(String extraWhere, String orderBy) {
        String normalizedWhere = extraWhere == null ? "" : extraWhere.strip();
        String normalizedOrderBy = orderBy == null || orderBy.isBlank() ? DEFAULT_ORDER_BY : orderBy.strip();

        return """
                select partner.id,
                       kind.code,
                       partner.code,
                       partner.name,
                       status.name,
                       status.code,
                       coalesce(source.name, 'Unassigned'),
                       partner.created_at,
                       partner.updated_at,
                       coalesce(email.value, ''),
                       coalesce(primary_person.first_name || ' ' || primary_person.last_name, ''),
                       coalesce(primary_person.email, ''),
                       coalesce(primary_person.phone, ''),
                       coalesce(primary_person.mobile, ''),
                       (
                         select count(*) from business_partner_contact_person person_count
                         where person_count.business_partner_id = partner.id and person_count.archived_at is null
                       ),
                       (
                         select count(*) from business_partner_document document_count
                         where document_count.business_partner_id = partner.id and document_count.archived_at is null
                       ),
                       coalesce(address.city, ''),
                       coalesce(address.country_code, ''),
                       left(coalesce(partner.notes, ''), 320)
                from business_partner partner
                join business_partner_kind kind on kind.id = partner.kind_id
                join business_partner_status status on status.id = partner.status_id
                left join lead_source source on source.id = partner.lead_source_id
                left join business_partner_contact_method email on email.business_partner_id = partner.id
                     and email.archived_at is null
                     and email.primary_contact = true
                     and email.contact_method_type_id = (select id from contact_method_type where code = 'EMAIL' limit 1)
                left join business_partner_contact_person primary_person on primary_person.business_partner_id = partner.id
                     and primary_person.archived_at is null
                     and primary_person.primary_contact = true
                left join business_partner_address partner_address on partner_address.business_partner_id = partner.id
                     and partner_address.archived_at is null
                     and partner_address.primary_address = true
                left join address address on address.id = partner_address.address_id and address.archived_at is null
                where partner.archived_at is null
                %s
                group by partner.id, kind.code, partner.code, partner.name, status.name, status.code, source.name,
                         partner.created_at, partner.updated_at, email.value, primary_person.first_name, primary_person.last_name,
                         primary_person.email, primary_person.phone, primary_person.mobile, address.city, address.country_code, partner.notes
                order by %s
                limit :limit
                """.formatted(normalizedWhere, normalizedOrderBy);
    }
}

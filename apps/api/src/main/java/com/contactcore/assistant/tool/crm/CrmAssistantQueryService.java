// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool.crm;

import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.assistant.retrieval.AssistantSearchResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CrmAssistantQueryService {
    private final EntityManager entityManager;
    private final CrmBusinessPartnerSqlBuilder businessPartnerSqlBuilder;

    public CrmAssistantQueryService(EntityManager entityManager, CrmBusinessPartnerSqlBuilder businessPartnerSqlBuilder) {
        this.entityManager = entityManager;
        this.businessPartnerSqlBuilder = businessPartnerSqlBuilder;
    }

    public List<AssistantSearchResult> searchRecords(String queryText, String kindCode, String source, int limit) {
        Query query = entityManager.createNativeQuery(businessPartnerSqlBuilder.buildSelect("""
                  and (:searchTerm = '' or (
                      lower(partner.code) like :searchLike
                      or lower(partner.name) like :searchLike
                      or lower(coalesce(partner.notes, '')) like :searchLike
                      or lower(coalesce(email.value, '')) like :searchLike
                      or exists (
                          select 1
                          from business_partner_contact_person search_person
                          where search_person.business_partner_id = partner.id
                            and search_person.archived_at is null
                            and (
                                lower(coalesce(search_person.first_name, '') || ' ' || coalesce(search_person.last_name, '')) like :searchLike
                                or lower(coalesce(search_person.email, '')) like :searchLike
                                or lower(coalesce(search_person.phone, '')) like :searchLike
                                or lower(coalesce(search_person.mobile, '')) like :searchLike
                            )
                      )
                      or lower(coalesce(source.name, '')) like :searchLike
                  ))
                  and (:kindCode = '' or kind.code = :kindCode)
                  and (:sourceTerm = '' or lower(coalesce(source.name, '')) like :sourceLike or lower(coalesce(source.code, '')) like :sourceLike)
                """, "partner.updated_at desc, partner.id desc"));
        applyCommonParameters(query, queryText, kindCode, source, limit, CommonParameterBinding.ALL);
        return rows(query).stream().map(this::businessPartnerResult).toList();
    }

    public List<AssistantSearchResult> businessPartnerDetails(String queryText, String kindCode, int limit) {
        Query query = entityManager.createNativeQuery(businessPartnerSqlBuilder.buildSelect("""
                  and (:searchTerm = '' or (
                      lower(partner.code) like :searchLike
                      or lower(partner.name) like :searchLike
                      or lower(coalesce(email.value, '')) like :searchLike
                      or lower(coalesce(primary_person.first_name, '') || ' ' || coalesce(primary_person.last_name, '')) like :searchLike
                  ))
                  and (:kindCode = '' or kind.code = :kindCode)
                  and (:sourceTerm = '' or true)
                """, "partner.updated_at desc, partner.id desc"));
        applyCommonParameters(query, queryText, kindCode, "", limit, CommonParameterBinding.SEARCH_ONLY);
        return rows(query).stream().map(this::businessPartnerResult).toList();
    }

    public List<AssistantSearchResult> staleLeads(int staleDays, String source, int limit) {
        Query query = entityManager.createNativeQuery(businessPartnerSqlBuilder.buildSelect("""
                  and kind.code = 'LEAD'
                  and status.code <> 'QUALIFIED'
                  and partner.updated_at < now() - (:staleDays * interval '1 day')
                  and (:searchTerm = '' or true)
                  and (:kindCode = '' or true)
                  and (:sourceTerm = '' or lower(coalesce(source.name, '')) like :sourceLike or lower(coalesce(source.code, '')) like :sourceLike)
                """, "partner.updated_at asc, partner.id asc"));
        applyCommonParameters(query, "", "", source, limit, CommonParameterBinding.SOURCE_ONLY);
        query.setParameter("staleDays", staleDays);
        return rows(query).stream().map(this::businessPartnerResult).toList();
    }

    public List<AssistantSearchResult> leadsWithoutContactPersons(String source, int limit) {
        Query query = entityManager.createNativeQuery(businessPartnerSqlBuilder.buildSelect("""
                  and kind.code = 'LEAD'
                  and not exists (
                      select 1 from business_partner_contact_person missing_person
                      where missing_person.business_partner_id = partner.id and missing_person.archived_at is null
                  )
                  and (:searchTerm = '' or true)
                  and (:kindCode = '' or true)
                  and (:sourceTerm = '' or lower(coalesce(source.name, '')) like :sourceLike or lower(coalesce(source.code, '')) like :sourceLike)
                """, "partner.updated_at desc, partner.id desc"));
        applyCommonParameters(query, "", "", source, limit, CommonParameterBinding.SOURCE_ONLY);
        return rows(query).stream().map(this::businessPartnerResult).toList();
    }

    public List<AssistantSearchResult> marketingSourcePerformance(int limit) {
        Query query = entityManager.createNativeQuery("""
                select source.id,
                       source.code,
                       source.name,
                       count(partner.id) as lead_count,
                       count(partner.id) filter (where status.code = 'QUALIFIED') as qualified_count,
                       count(partner.id) filter (where status.code <> 'QUALIFIED') as open_count,
                       round(case when count(partner.id) = 0 then 0 else count(partner.id) filter (where status.code = 'QUALIFIED') * 100.0 / count(partner.id) end, 2) as qualified_rate
                from lead_source source
                left join business_partner partner on partner.lead_source_id = source.id
                     and partner.archived_at is null
                     and exists (
                         select 1 from business_partner_kind lead_kind
                         where lead_kind.id = partner.kind_id and lead_kind.code = 'LEAD'
                     )
                left join business_partner_status status on status.id = partner.status_id
                where source.archived_at is null
                group by source.id, source.code, source.name, source.sort_order
                order by lead_count desc, qualified_count desc, source.sort_order asc, source.name asc
                limit :limit
                """);
        query.setParameter("limit", limit);
        return rows(query).stream().map(row -> {
            Long sourceId = number(row[0]);
            String code = text(row[1]);
            String name = text(row[2]);
            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("Entity", "Marketing source");
            fields.put("Code", code);
            fields.put("Name", name);
            fields.put("Lead count", text(row[3]));
            fields.put("Qualified leads", text(row[4]));
            fields.put("Open leads", text(row[5]));
            fields.put("Qualified rate percent", text(row[6]));
            return AssistantSearchResult.of(new AssistantRecordReference("MARKETING_SOURCE", sourceId, name, "/marketing-sources"), fields);
        }).toList();
    }

    public List<AssistantSearchResult> crmSummary() {
        Query query = entityManager.createNativeQuery("""
                select kind.id,
                       kind.code,
                       kind.name,
                       count(partner.id) as active_records,
                       count(partner.id) filter (where status.code = 'QUALIFIED') as qualified_records,
                       count(partner.id) filter (where partner.created_at >= now() - interval '30 days') as created_last_30_days
                from business_partner_kind kind
                left join business_partner partner on partner.kind_id = kind.id and partner.archived_at is null
                left join business_partner_status status on status.id = partner.status_id
                where kind.archived_at is null
                group by kind.id, kind.code, kind.name
                order by kind.name asc
                """);
        return rows(query).stream().map(row -> {
            String code = text(row[1]);
            String name = text(row[2]);
            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("Entity", "CRM summary");
            fields.put("Kind", name);
            fields.put("Kind code", code);
            fields.put("Active records", text(row[3]));
            fields.put("Qualified records", text(row[4]));
            fields.put("Created in last 30 days", text(row[5]));
            return AssistantSearchResult.of(new AssistantRecordReference("CRM_SUMMARY", number(row[0]), name, routeForKind(code)), fields);
        }).toList();
    }

    public List<AssistantSearchResult> contactCoverage() {
        Query query = entityManager.createNativeQuery("""
                select kind.id,
                       kind.code,
                       kind.name,
                       count(partner.id) as active_records,
                       count(partner.id) filter (where exists (
                           select 1 from business_partner_contact_person person
                           where person.business_partner_id = partner.id and person.archived_at is null
                       )) as with_contacts,
                       count(partner.id) filter (where partner.id is not null and not exists (
                           select 1 from business_partner_contact_person person
                           where person.business_partner_id = partner.id and person.archived_at is null
                       )) as without_contacts
                from business_partner_kind kind
                left join business_partner partner on partner.kind_id = kind.id and partner.archived_at is null
                where kind.archived_at is null
                group by kind.id, kind.code, kind.name
                order by without_contacts desc, kind.name asc
                """);
        return rows(query).stream().map(row -> {
            String code = text(row[1]);
            String name = text(row[2]);
            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("Entity", "Contact coverage");
            fields.put("Kind", name);
            fields.put("Kind code", code);
            fields.put("Active records", text(row[3]));
            fields.put("Records with contact persons", text(row[4]));
            fields.put("Records without contact persons", text(row[5]));
            return AssistantSearchResult.of(new AssistantRecordReference("CONTACT_COVERAGE", number(row[0]), name, routeForKind(code)), fields);
        }).toList();
    }

    public List<AssistantSearchResult> recentRecords(String kindCode, int limit) {
        Query query = entityManager.createNativeQuery(businessPartnerSqlBuilder.buildSelect("""
                  and (:searchTerm = '' or true)
                  and (:kindCode = '' or kind.code = :kindCode)
                  and (:sourceTerm = '' or true)
                """, "partner.created_at desc, partner.id desc"));
        applyCommonParameters(query, "", kindCode, "", limit, CommonParameterBinding.BASIC);
        return rows(query).stream().map(this::businessPartnerResult).toList();
    }

    public List<AssistantSearchResult> statusBreakdown() {
        Query query = entityManager.createNativeQuery("""
                select kind.id,
                       kind.code,
                       kind.name,
                       status.code,
                       status.name,
                       count(partner.id) as record_count
                from business_partner_kind kind
                cross join business_partner_status status
                left join business_partner partner on partner.kind_id = kind.id
                     and partner.status_id = status.id
                     and partner.archived_at is null
                where kind.archived_at is null and status.archived_at is null
                group by kind.id, kind.code, kind.name, status.code, status.name
                having count(partner.id) > 0
                order by kind.name asc, record_count desc, status.name asc
                """);
        return rows(query).stream().map(row -> {
            String kindCode = text(row[1]);
            String statusCode = text(row[3]);
            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("Entity", "Status breakdown");
            fields.put("Kind", text(row[2]));
            fields.put("Kind code", kindCode);
            fields.put("Status", text(row[4]));
            fields.put("Status code", statusCode);
            fields.put("Record count", text(row[5]));
            return AssistantSearchResult.of(
                    new AssistantRecordReference("STATUS_BREAKDOWN", number(row[0]), text(row[2]) + " / " + text(row[4]), routeForKind(kindCode)),
                    fields
            );
        }).toList();
    }

    public List<AssistantSearchResult> leadPipeline() {
        Query query = entityManager.createNativeQuery("""
                select status.id,
                       status.code,
                       status.name,
                       count(partner.id) as lead_count,
                       count(partner.id) filter (where partner.updated_at < now() - interval '14 days') as stale_count,
                       count(partner.id) filter (where not exists (
                           select 1 from business_partner_contact_person person
                           where person.business_partner_id = partner.id and person.archived_at is null
                       )) as missing_contacts
                from business_partner_status status
                left join business_partner partner on partner.status_id = status.id
                     and partner.archived_at is null
                     and exists (
                         select 1 from business_partner_kind kind
                         where kind.id = partner.kind_id and kind.code = 'LEAD'
                     )
                where status.archived_at is null
                group by status.id, status.code, status.name
                having count(partner.id) > 0
                order by lead_count desc, status.name asc
                """);
        return rows(query).stream().map(row -> {
            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("Entity", "Lead pipeline");
            fields.put("Status", text(row[2]));
            fields.put("Status code", text(row[1]));
            fields.put("Lead count", text(row[3]));
            fields.put("Stale leads", text(row[4]));
            fields.put("Leads without contact persons", text(row[5]));
            return AssistantSearchResult.of(new AssistantRecordReference("LEAD_PIPELINE", number(row[0]), text(row[2]), "/leads"), fields);
        }).toList();
    }

    private AssistantSearchResult businessPartnerResult(Object[] row) {
        Long partnerId = number(row[0]);
        String kindCode = text(row[1]);
        String code = text(row[2]);
        String name = text(row[3]);
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("Entity", "Business partner");
        fields.put("Kind", kindCode);
        fields.put("Code", code);
        fields.put("Name", name);
        fields.put("Status", text(row[4]));
        fields.put("Status code", text(row[5]));
        fields.put("Marketing source", text(row[6]));
        fields.put("Created at", text(row[7]));
        fields.put("Last updated", text(row[8]));
        fields.put("Primary email", text(row[9]));
        fields.put("Primary contact", text(row[10]));
        fields.put("Primary contact email", text(row[11]));
        fields.put("Primary contact phone", firstNonBlank(text(row[12]), text(row[13])));
        fields.put("Contact persons", text(row[14]));
        fields.put("Documents", text(row[15]));
        fields.put("City", text(row[16]));
        fields.put("Country", text(row[17]));
        fields.put("Notes excerpt", text(row[18]));
        return AssistantSearchResult.of(
                new AssistantRecordReference("BUSINESS_PARTNER", partnerId, code + " - " + name, routeFor(kindCode, partnerId)),
                fields
        );
    }

    private void applyCommonParameters(Query query,
                                       String queryText,
                                       String kindCode,
                                       String source,
                                       int limit,
                                       CommonParameterBinding binding) {
        String searchTerm = normalize(queryText);
        String sourceTerm = normalize(source);
        query.setParameter("searchTerm", searchTerm);
        if (binding.bindSearchLike()) {
            query.setParameter("searchLike", "%" + searchTerm + "%");
        }
        query.setParameter("kindCode", kindCode == null ? "" : kindCode.trim().toUpperCase(Locale.ROOT));
        query.setParameter("sourceTerm", sourceTerm);
        if (binding.bindSourceLike()) {
            query.setParameter("sourceLike", "%" + sourceTerm + "%");
        }
        query.setParameter("limit", Math.max(1, limit));
    }

    private enum CommonParameterBinding {
        ALL(true, true),
        SEARCH_ONLY(true, false),
        SOURCE_ONLY(false, true),
        BASIC(false, false);

        private final boolean bindSearchLike;
        private final boolean bindSourceLike;

        CommonParameterBinding(boolean bindSearchLike, boolean bindSourceLike) {
            this.bindSearchLike = bindSearchLike;
            this.bindSourceLike = bindSourceLike;
        }

        boolean bindSearchLike() {
            return bindSearchLike;
        }

        boolean bindSourceLike() {
            return bindSourceLike;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> rows(Query query) {
        return ((List<Object[]>) query.getResultList());
    }

    private Long number(Object value) {
        return ((Number) value).longValue();
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String firstNonBlank(String first, String second) {
        return first == null || first.isBlank() ? second : first;
    }

    private String routeFor(String kindCode, Long id) {
        return switch (kindCode) {
            case "CUSTOMER" -> "/customers/" + id;
            case "SUPPLIER" -> "/suppliers/" + id;
            default -> "/leads/" + id;
        };
    }

    private String routeForKind(String kindCode) {
        return switch (kindCode) {
            case "CUSTOMER" -> "/customers";
            case "SUPPLIER" -> "/suppliers";
            case "LEAD" -> "/leads";
            default -> "/reports";
        };
    }
}

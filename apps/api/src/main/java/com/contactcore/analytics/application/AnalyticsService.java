// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.analytics.application;

import com.contactcore.analytics.api.ChartPointResponse;
import com.contactcore.analytics.api.CrmReportResponse;
import com.contactcore.analytics.api.DashboardResponse;
import com.contactcore.analytics.api.KpiResponse;
import com.contactcore.analytics.api.MarketingSourceReportRowResponse;
import com.contactcore.analytics.api.MonthlyCountResponse;
import com.contactcore.analytics.api.RecentBusinessPartnerResponse;
import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {
    private final EntityManager entityManager;

    public AnalyticsService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        long customers = countActiveByKind("CUSTOMER");
        long leads = countActiveByKind("LEAD");
        long suppliers = countActiveByKind("SUPPLIER");
        long qualifiedLeads = countActiveLeadsByStatus("QUALIFIED");
        long totalBusinessPartners = customers + leads + suppliers;
        long contactPersons = scalarLong("""
                select count(*) from business_partner_contact_person person where person.archived_at is null
                """);
        long partnersWithContacts = scalarLong("""
                select count(distinct partner.id)
                from business_partner partner
                join business_partner_contact_person person on person.business_partner_id = partner.id and person.archived_at is null
                where partner.archived_at is null
                """);

        return new DashboardResponse(
                List.of(
                        new KpiResponse("totalBusinessPartners", "Active records", totalBusinessPartners, "records", "Customers, leads, and suppliers that are not archived."),
                        new KpiResponse("customers", "Customers", customers, "records", "Active customer records."),
                        new KpiResponse("leads", "Open leads", leads, "records", "Active lead records."),
                        new KpiResponse("contactPersons", "Contact persons", contactPersons, "contacts", "Active contact persons linked to CRM records."),
                        new KpiResponse("leadQualificationRate", "Lead qualification", AnalyticsMath.percentage(qualifiedLeads, leads), "%", "Qualified leads divided by active leads."),
                        new KpiResponse("contactCoverage", "Contact coverage", AnalyticsMath.percentage(partnersWithContacts, totalBusinessPartners), "%", "Active records with at least one contact person.")
                ),
                chart("""
                        select kind.name, count(*)
                        from business_partner partner
                        join business_partner_kind kind on kind.id = partner.kind_id
                        where partner.archived_at is null
                        group by kind.name
                        order by count(*) desc, kind.name asc
                        """),
                chart("""
                        select coalesce(source.name, 'Unassigned'), count(*)
                        from business_partner partner
                        join business_partner_kind kind on kind.id = partner.kind_id
                        left join lead_source source on source.id = partner.lead_source_id
                        where partner.archived_at is null and kind.code = 'LEAD'
                        group by coalesce(source.name, 'Unassigned')
                        order by count(*) desc, coalesce(source.name, 'Unassigned') asc
                        """),
                chart("""
                        select status.name, count(*)
                        from business_partner partner
                        join business_partner_status status on status.id = partner.status_id
                        where partner.archived_at is null
                        group by status.name
                        order by count(*) desc, status.name asc
                        """),
                monthlyNewBusinessPartners(),
                contactPersonsByRole(),
                contactCoverageByKind(),
                recentBusinessPartners(8)
        );
    }

    @Transactional(readOnly = true)
    public CrmReportResponse crmReport() {
        long customers = countActiveByKind("CUSTOMER");
        long leads = countActiveByKind("LEAD");
        long suppliers = countActiveByKind("SUPPLIER");
        long qualifiedLeads = countActiveLeadsByStatus("QUALIFIED");
        long staleLeads = countStaleLeads();
        long contactPersons = scalarLong("""
                select count(*) from business_partner_contact_person person where person.archived_at is null
                """);

        return new CrmReportResponse(
                List.of(
                        new KpiResponse("customers", "Customers", customers, "records", "Current active customers."),
                        new KpiResponse("leads", "Leads", leads, "records", "Current active leads."),
                        new KpiResponse("suppliers", "Suppliers", suppliers, "records", "Current active suppliers."),
                        new KpiResponse("contactPersons", "Contact persons", contactPersons, "contacts", "Active contact persons."),
                        new KpiResponse("staleLeads", "Stale leads", staleLeads, "records", "Leads older than 30 days that are not qualified."),
                        new KpiResponse("leadQualificationRate", "Lead qualification", AnalyticsMath.percentage(qualifiedLeads, leads), "%", "Qualified leads divided by active leads.")
                ),
                chart("""
                        select kind.name, count(*)
                        from business_partner partner
                        join business_partner_kind kind on kind.id = partner.kind_id
                        where partner.archived_at is null
                        group by kind.name
                        order by count(*) desc, kind.name asc
                        """),
                chart("""
                        select status.name, count(*)
                        from business_partner partner
                        join business_partner_status status on status.id = partner.status_id
                        where partner.archived_at is null
                        group by status.name
                        order by count(*) desc, status.name asc
                        """),
                chart("""
                        select coalesce(source.name, 'Unassigned'), count(*)
                        from business_partner partner
                        left join lead_source source on source.id = partner.lead_source_id
                        where partner.archived_at is null
                        group by coalesce(source.name, 'Unassigned')
                        order by count(*) desc, coalesce(source.name, 'Unassigned') asc
                        """),
                contactPersonsByRole(),
                contactCoverageByKind(),
                marketingSourcePerformance(),
                recentBusinessPartners(20)
        );
    }

    private long countActiveByKind(String kindCode) {
        return scalarLong("""
                select count(*)
                from business_partner partner
                join business_partner_kind kind on kind.id = partner.kind_id
                where partner.archived_at is null and kind.code = :kindCode
                """, kindCode);
    }

    private long countActiveLeadsByStatus(String statusCode) {
        Number result = (Number) entityManager.createNativeQuery("""
                        select count(*)
                        from business_partner partner
                        join business_partner_kind kind on kind.id = partner.kind_id
                        join business_partner_status status on status.id = partner.status_id
                        where partner.archived_at is null and kind.code = 'LEAD' and status.code = :statusCode
                        """)
                .setParameter("statusCode", statusCode)
                .getSingleResult();
        return result.longValue();
    }

    private long countStaleLeads() {
        return scalarLong("""
                select count(*)
                from business_partner partner
                join business_partner_kind kind on kind.id = partner.kind_id
                join business_partner_status status on status.id = partner.status_id
                where partner.archived_at is null
                  and kind.code = 'LEAD'
                  and status.code <> 'QUALIFIED'
                  and partner.created_at < now() - interval '30 days'
                """);
    }

    private long scalarLong(String sql) {
        Number result = (Number) entityManager.createNativeQuery(sql).getSingleResult();
        return result.longValue();
    }

    private long scalarLong(String sql, String kindCode) {
        Number result = (Number) entityManager.createNativeQuery(sql)
                .setParameter("kindCode", kindCode)
                .getSingleResult();
        return result.longValue();
    }

    private List<ChartPointResponse> chart(String sql) {
        return queryRows(sql).stream()
                .map(row -> new ChartPointResponse(String.valueOf(row[0]), toLong(row[1])))
                .toList();
    }

    private List<MonthlyCountResponse> monthlyNewBusinessPartners() {
        return queryRows(entityManager.createNativeQuery("""
                        select to_char(date_trunc('month', partner.created_at), 'YYYY-MM') as month_label, count(*)
                        from business_partner partner
                        where partner.archived_at is null
                          and partner.created_at >= date_trunc('month', now()) - interval '11 months'
                        group by date_trunc('month', partner.created_at)
                        order by date_trunc('month', partner.created_at) asc
                        """).getResultList()).stream()
                .map(row -> new MonthlyCountResponse(String.valueOf(row[0]), toLong(row[1])))
                .toList();
    }

    private List<ChartPointResponse> contactPersonsByRole() {
        return chart("""
                select coalesce(nullif(person.role_title, ''), 'Unassigned'), count(*)
                from business_partner_contact_person person
                where person.archived_at is null
                group by coalesce(nullif(person.role_title, ''), 'Unassigned')
                order by count(*) desc, coalesce(nullif(person.role_title, ''), 'Unassigned') asc
                limit 10
                """);
    }

    private List<ChartPointResponse> contactCoverageByKind() {
        return queryRows("""
                select kind.name,
                       case when count(partner.id) = 0 then 0
                            else round((count(distinct person.business_partner_id)::numeric / count(distinct partner.id)::numeric) * 100, 1)
                       end as coverage
                from business_partner_kind kind
                left join business_partner partner on partner.kind_id = kind.id and partner.archived_at is null
                left join business_partner_contact_person person on person.business_partner_id = partner.id and person.archived_at is null
                where kind.archived_at is null
                group by kind.name
                order by kind.name asc
                """).stream()
                .map(row -> new ChartPointResponse(String.valueOf(row[0]), toDoubleAsLong(row[1])))
                .toList();
    }

    private List<MarketingSourceReportRowResponse> marketingSourcePerformance() {
        return queryRows(entityManager.createNativeQuery("""
                        select source.name as marketing_source,
                               count(*) filter (where kind.code = 'LEAD') as leads,
                               count(*) filter (where kind.code = 'LEAD' and status.code = 'QUALIFIED') as qualified_leads,
                               count(*) filter (where kind.code = 'CUSTOMER') as customers
                        from lead_source source
                        left join business_partner partner on partner.lead_source_id = source.id and partner.archived_at is null
                        left join business_partner_kind kind on kind.id = partner.kind_id
                        left join business_partner_status status on status.id = partner.status_id
                        where source.archived_at is null
                        group by source.name, source.sort_order
                        order by leads desc, customers desc, source.sort_order asc, source.name asc
                        """).getResultList()).stream()
                .map(row -> {
                    long leads = toLong(row[1]);
                    long qualifiedLeads = toLong(row[2]);
                    long customers = toLong(row[3]);
                    return new MarketingSourceReportRowResponse(
                            String.valueOf(row[0]),
                            leads,
                            qualifiedLeads,
                            customers,
                            AnalyticsMath.percentage(qualifiedLeads, leads)
                    );
                })
                .toList();
    }

    private List<RecentBusinessPartnerResponse> recentBusinessPartners(int limit) {
        return queryRows(entityManager.createNativeQuery("""
                        select partner.id,
                               kind.code,
                               partner.code,
                               partner.name,
                               status.name,
                               coalesce(source.name, 'Unassigned'),
                               partner.created_at
                        from business_partner partner
                        join business_partner_kind kind on kind.id = partner.kind_id
                        join business_partner_status status on status.id = partner.status_id
                        left join lead_source source on source.id = partner.lead_source_id
                        where partner.archived_at is null
                        order by partner.created_at desc, partner.id desc
                        limit :limit
                        """)
                .setParameter("limit", limit)
                .getResultList()).stream()
                .map(row -> new RecentBusinessPartnerResponse(
                        toLong(row[0]),
                        String.valueOf(row[1]),
                        String.valueOf(row[2]),
                        String.valueOf(row[3]),
                        String.valueOf(row[4]),
                        String.valueOf(row[5]),
                        toInstant(row[6])
                ))
                .toList();
    }

    private List<Object[]> queryRows(String sql) {
        return queryRows(entityManager.createNativeQuery(sql).getResultList());
    }

    private static List<Object[]> queryRows(List<?> resultRows) {
        return resultRows.stream()
                .map(AnalyticsService::requireRow)
                .toList();
    }

    private static Object[] requireRow(Object value) {
        if (value instanceof Object[] row) {
            return row;
        }
        throw new IllegalStateException("Expected native query row to be Object[] but got: " + value.getClass().getName());
    }

    private static long toLong(Object value) {
        return ((Number) value).longValue();
    }

    private static long toDoubleAsLong(Object value) {
        return Math.round(((Number) value).doubleValue());
    }

    private static Instant toInstant(Object value) {
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toInstant();
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant();
        }
        throw new IllegalStateException("Unsupported timestamp type: " + value.getClass().getName());
    }
}

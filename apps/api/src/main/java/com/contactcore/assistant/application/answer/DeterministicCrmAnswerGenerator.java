// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.answer;

import com.contactcore.assistant.application.AssistantLocaleContext;
import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRecordReference;
import com.contactcore.assistant.retrieval.AssistantRetrievalResult;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.retrieval.AssistantSearchResult;
import com.contactcore.assistant.tool.AssistantToolResult;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DeterministicCrmAnswerGenerator implements AssistantAnswerGenerator {
    public static final String MODEL_NAME = "contactcore-deterministic";
    private static final int MAX_RECORDS_IN_TEXT = 8;
    private static final Set<AssistantIntent> DETERMINISTIC_INTENTS = EnumSet.of(
            AssistantIntent.GREETING,
            AssistantIntent.ASSISTANT_IDENTITY,
            AssistantIntent.ASSISTANT_CAPABILITIES,
            AssistantIntent.UNCLEAR_REQUEST,
            AssistantIntent.UNSUPPORTED,
            AssistantIntent.BUSINESS_PARTNER_EXISTENCE,
            AssistantIntent.BUSINESS_PARTNER_SEARCH,
            AssistantIntent.BUSINESS_PARTNER_DETAILS,
            AssistantIntent.LEADS_MISSING_CONTACTS,
            AssistantIntent.LEADS_NEED_FOLLOW_UP,
            AssistantIntent.MARKETING_PERFORMANCE,
            AssistantIntent.CONTACT_COVERAGE,
            AssistantIntent.RECENT_RECORDS,
            AssistantIntent.STATUS_BREAKDOWN,
            AssistantIntent.LEAD_PIPELINE
    );
    private static final Set<AssistantRetrievalType> SUPPORTED_TYPES = EnumSet.of(
            AssistantRetrievalType.ASSISTANT_HELP,
            AssistantRetrievalType.CRM_SEARCH,
            AssistantRetrievalType.BUSINESS_PARTNER_DETAILS,
            AssistantRetrievalType.STALE_LEADS,
            AssistantRetrievalType.LEADS_WITHOUT_CONTACTS,
            AssistantRetrievalType.MARKETING_SOURCE_ANALYTICS,
            AssistantRetrievalType.CONTACT_COVERAGE,
            AssistantRetrievalType.RECENT_RECORDS,
            AssistantRetrievalType.STATUS_BREAKDOWN,
            AssistantRetrievalType.LEAD_PIPELINE
    );

    private final AssistantStaticResponseCatalog staticResponses;

    public DeterministicCrmAnswerGenerator(AssistantStaticResponseCatalog staticResponses) {
        this.staticResponses = staticResponses;
    }

    @Override
    public boolean supports(AssistantPlan plan, AssistantRetrievalResult retrieval) {
        return DETERMINISTIC_INTENTS.contains(plan.intent()) || SUPPORTED_TYPES.contains(plan.retrievalType());
    }

    public AssistantAnswerGenerationResult generate(AssistantPlan plan, AssistantRetrievalResult retrieval, String userMessage) {
        return generate(plan, retrieval, userMessage, new AssistantLocaleContext(com.contactcore.shared.localization.SupportedLocale.DEFAULT, com.contactcore.shared.localization.SupportedLocale.DEFAULT.languageName(), com.contactcore.shared.localization.SupportedLocale.DEFAULT.direction()));
    }

    @Override
    public AssistantAnswerGenerationResult generate(AssistantPlan plan, AssistantRetrievalResult retrieval, String userMessage, AssistantLocaleContext locale) {
        return AssistantAnswerGenerationResult.success(AssistantAnswerSource.DETERMINISTIC, buildAnswer(plan, retrieval, locale), MODEL_NAME);
    }

    String buildAnswer(AssistantPlan plan, AssistantRetrievalResult retrieval, AssistantLocaleContext locale) {
        if (plan.intent() == AssistantIntent.GREETING) {
            return staticResponses.greetingAnswer(locale);
        }
        if (plan.intent() == AssistantIntent.ASSISTANT_IDENTITY) {
            return staticResponses.identityAnswer(locale);
        }
        if (plan.intent() == AssistantIntent.ASSISTANT_CAPABILITIES) {
            return staticResponses.capabilitiesAnswer(locale);
        }
        if (plan.intent() == AssistantIntent.UNCLEAR_REQUEST) {
            if (plan.userIntent().startsWith("UNCLEAR_REQUEST conflicting business partner types")) {
                return staticResponses.ambiguousBusinessPartnerTypeAnswer(locale);
            }
            return staticResponses.unclearRequestAnswer(locale);
        }
        if (plan.intent() == AssistantIntent.UNSUPPORTED) {
            return staticResponses.unsupportedAnswer(locale);
        }
        if (plan.intent() == AssistantIntent.BUSINESS_PARTNER_EXISTENCE) {
            return existenceAnswer(plan, retrieval.results(), locale);
        }

        List<AssistantSearchResult> records = retrieval.results();
        if (records.isEmpty()) {
            return emptyAnswer(plan, locale);
        }

        String heading = headingFor(plan, records.size(), locale);
        String recordLines = records.stream()
                .limit(MAX_RECORDS_IN_TEXT)
                .map(result -> recordLine(result, locale))
                .collect(Collectors.joining("\n"));
        String truncationNotice = records.size() > MAX_RECORDS_IN_TEXT
                ? "\n\n" + message(locale, "assistant.answer.truncated", "Only the first {0} records are shown here. Use the references to inspect the rest.", MAX_RECORDS_IN_TEXT)
                : "";
        String toolSummary = toolSummary(retrieval.toolResults(), locale);

        return """
                %s

                %s
                %s%s

                %s%s
                """.formatted(
                heading,
                message(locale, "assistant.answer.keyRecords", "Key records:"),
                recordLines,
                truncationNotice,
                toolSummary,
                suggestedAction(locale, "assistant.answer.openReferences", "Open the referenced source records and review the relevant data manually.")
        ).trim();
    }

    private String existenceAnswer(AssistantPlan plan, List<AssistantSearchResult> records, AssistantLocaleContext locale) {
        String target = targetLabel(plan);
        if (records.isEmpty()) {
            return """
                    %s

                    %s
                    """.formatted(
                    message(locale, "assistant.answer.existence.notFound", "No. I did not find an active {0} matching \"{1}\".", kindLabel(plan, locale), target),
                    suggestedAction(locale, "assistant.answer.checkSpelling", "Check spelling, archived records, or try searching by code, email, or contact person.")
            ).trim();
        }

        String recordLines = records.stream()
                .limit(MAX_RECORDS_IN_TEXT)
                .map(result -> recordLine(result, locale))
                .collect(Collectors.joining("\n"));
        String truncationNotice = records.size() > MAX_RECORDS_IN_TEXT
                ? "\n\n" + message(locale, "assistant.answer.truncated", "Only the first {0} records are shown here. Use the references to inspect the rest.", MAX_RECORDS_IN_TEXT)
                : "";

        return """
                %s

                %s
                %s%s

                %s
                """.formatted(
                message(locale, "assistant.answer.existence.found", "Yes. I found {0} active {1} matching \"{2}\".", records.size(), kindLabel(plan, locale), target),
                message(locale, "assistant.answer.keyRecords", "Key records:"),
                recordLines,
                truncationNotice,
                suggestedAction(locale, "assistant.answer.openReferenceToVerify", "Open the referenced source records to verify the details.")
        ).trim();
    }

    private String emptyAnswer(AssistantPlan plan, AssistantLocaleContext locale) {
        return switch (plan.intent()) {
            case LEADS_MISSING_CONTACTS -> """
                    %s

                    %s
                    """.formatted(
                    message(locale, "assistant.answer.empty.leadsMissingContacts", "I did not find active leads without contact persons."),
                    suggestedAction(locale, "assistant.answer.empty.leadsMissingContactsAction", "Check archived leads or verify that the relevant leads exist if you expected results.")
            ).trim();
            case LEADS_NEED_FOLLOW_UP -> """
                    %s

                    %s
                    """.formatted(
                    message(locale, "assistant.answer.empty.staleLeads", "I did not find active leads that currently need follow-up based on the configured stale-lead criteria."),
                    suggestedAction(locale, "assistant.answer.empty.staleLeadsAction", "Review the lead follow-up threshold if you expected older open leads to appear.")
            ).trim();
            case BUSINESS_PARTNER_SEARCH, BUSINESS_PARTNER_DETAILS -> """
                    %s

                    %s
                    """.formatted(
                    message(locale, "assistant.answer.empty.businessPartner", "I did not find matching active CRM records for \"{0}\".", targetLabel(plan)),
                    suggestedAction(locale, "assistant.answer.trySpecific", "Try a more specific name, code, email, contact person, or marketing source.")
            ).trim();
            default -> """
                    %s

                    %s
                    """.formatted(
                    message(locale, "assistant.answer.noMatchingData", "I did not find matching active CRM data for this request."),
                    suggestedAction(locale, "assistant.answer.checkArchived", "Check whether matching records are archived or whether the CRM data exists.")
            ).trim();
        };
    }

    private String headingFor(AssistantPlan plan, int recordCount, AssistantLocaleContext locale) {
        return switch (plan.intent()) {
            case LEADS_MISSING_CONTACTS -> message(locale, "assistant.answer.heading.leadsMissingContacts", "I found {0} active leads without contact persons.", recordCount);
            case LEADS_NEED_FOLLOW_UP -> message(locale, "assistant.answer.heading.staleLeads", "I found {0} leads that may need follow-up.", recordCount);
            case MARKETING_PERFORMANCE -> message(locale, "assistant.answer.heading.marketing", "I found marketing-source performance data for {0} sources.", recordCount);
            case CONTACT_COVERAGE -> message(locale, "assistant.answer.heading.contactCoverage", "I found contact-coverage data for {0} CRM categories.", recordCount);
            case RECENT_RECORDS -> message(locale, "assistant.answer.heading.recentRecords", "I found {0} recently created CRM records.", recordCount);
            case STATUS_BREAKDOWN -> message(locale, "assistant.answer.heading.statusBreakdown", "I found {0} CRM status breakdown records.", recordCount);
            case LEAD_PIPELINE -> message(locale, "assistant.answer.heading.leadPipeline", "I found lead-pipeline data for {0} status groups.", recordCount);
            case BUSINESS_PARTNER_SEARCH -> message(locale, "assistant.answer.heading.search", "I found {0} matching active CRM records for \"{1}\".", recordCount, targetLabel(plan));
            case BUSINESS_PARTNER_DETAILS -> message(locale, "assistant.answer.heading.details", "I found details for {0} active CRM records matching \"{1}\".", recordCount, targetLabel(plan));
            default -> message(locale, "assistant.answer.heading.generic", "I found {0} matching CRM records.", recordCount);
        };
    }

    private String recordLine(AssistantSearchResult result, AssistantLocaleContext locale) {
        AssistantRecordReference reference = result.reference();
        Map<String, String> fields = result.fields();
        StringBuilder builder = new StringBuilder("- ").append(reference.label());

        appendField(builder, fields, "Kind", fieldLabel(locale, "kind", "kind"), locale);
        appendField(builder, fields, "Status", fieldLabel(locale, "status", "status"), locale);
        appendField(builder, fields, "Marketing source", fieldLabel(locale, "source", "source"), locale);
        appendField(builder, fields, "Primary email", fieldLabel(locale, "email", "email"), locale);
        appendField(builder, fields, "Primary phone", fieldLabel(locale, "phone", "phone"), locale);
        appendField(builder, fields, "Website", fieldLabel(locale, "website", "website"), locale);
        appendField(builder, fields, "External code", fieldLabel(locale, "externalCode", "external code"), locale);
        appendField(builder, fields, "Source system", fieldLabel(locale, "sourceSystem", "source system"), locale);
        appendField(builder, fields, "Connector", fieldLabel(locale, "connector", "connector"), locale);
        appendField(builder, fields, "Currency", fieldLabel(locale, "currency", "currency"), locale);
        appendField(builder, fields, "Balance", fieldLabel(locale, "balance", "balance"), locale);
        appendField(builder, fields, "Primary contact", fieldLabel(locale, "primaryContact", "primary contact"), locale);
        appendField(builder, fields, "Contact persons", fieldLabel(locale, "contactPersons", "contact persons"), locale);
        appendField(builder, fields, "Lead count", fieldLabel(locale, "leadCount", "lead count"), locale);
        appendField(builder, fields, "Qualified leads", fieldLabel(locale, "qualifiedLeads", "qualified leads"), locale);
        appendField(builder, fields, "Open leads", fieldLabel(locale, "openLeads", "open leads"), locale);
        appendField(builder, fields, "Records without contact persons", fieldLabel(locale, "withoutContacts", "without contacts"), locale);
        appendField(builder, fields, "Record count", fieldLabel(locale, "recordCount", "record count"), locale);
        appendField(builder, fields, "Active records", fieldLabel(locale, "activeRecords", "active records"), locale);
        appendField(builder, fields, "Qualified records", fieldLabel(locale, "qualifiedRecords", "qualified records"), locale);
        appendField(builder, fields, "Created in last 30 days", fieldLabel(locale, "createdLast30Days", "created last 30 days"), locale);
        appendField(builder, fields, "Stale leads", fieldLabel(locale, "staleLeads", "stale leads"), locale);
        appendField(builder, fields, "Leads without contact persons", fieldLabel(locale, "leadsWithoutContactPersons", "leads without contact persons"), locale);
        appendField(builder, fields, "Last updated", fieldLabel(locale, "lastUpdated", "last updated"), locale);

        return builder.toString();
    }

    private void appendField(StringBuilder builder, Map<String, String> fields, String key, String label, AssistantLocaleContext locale) {
        String value = fields.get(key);
        if (value != null && !value.isBlank()) {
            builder.append(builder.indexOf(" — ") < 0 ? " — " : ", ")
                    .append(label)
                    .append(": ")
                    .append(localizedFieldValue(key, value, locale));
        }
    }

    private String localizedFieldValue(String key, String value, AssistantLocaleContext locale) {
        if ("Kind".equals(key)) {
            String normalized = value.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
            return message(locale, "assistant.kind." + normalized, value);
        }
        return value;
    }

    private String toolSummary(List<AssistantToolResult> toolResults, AssistantLocaleContext locale) {
        String summary = toolResults.stream()
                .map(AssistantToolResult::summary)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .collect(Collectors.joining(" "));
        return summary.isBlank() ? "" : message(locale, "assistant.answer.toolSummary", "Tool summary: {0}", summary) + "\n\n";
    }

    private String kindLabel(AssistantPlan plan, AssistantLocaleContext locale) {
        if (plan.kindCode().isBlank()) {
            return message(locale, "assistant.kind.crmRecord", "CRM record");
        }
        return message(locale, "assistant.kind." + plan.kindCode(), plan.kindCode().toLowerCase(Locale.ROOT).replace('_', ' '));
    }

    private String fieldLabel(AssistantLocaleContext locale, String key, String fallback) {
        return message(locale, "assistant.field." + key, fallback);
    }

    private String suggestedAction(AssistantLocaleContext locale, String actionKey, String actionFallback) {
        return message(locale, "assistant.answer.suggestedAction", "Suggested next manual action:") + "\n" + message(locale, actionKey, actionFallback);
    }

    private String message(AssistantLocaleContext locale, String key, String fallback, Object... args) {
        return staticResponses.message(locale, key, fallback, args);
    }

    private String targetLabel(AssistantPlan plan) {
        if (!plan.searchTerm().isBlank()) {
            return plan.searchTerm();
        }
        return plan.normalizedQuery().isBlank() ? "the requested value" : plan.normalizedQuery();
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.answer;

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

    @Override
    public AssistantAnswerGenerationResult generate(AssistantPlan plan, AssistantRetrievalResult retrieval, String userMessage) {
        return AssistantAnswerGenerationResult.success(AssistantAnswerSource.DETERMINISTIC, buildAnswer(plan, retrieval), MODEL_NAME);
    }

    String buildAnswer(AssistantPlan plan, AssistantRetrievalResult retrieval) {
        if (plan.intent() == AssistantIntent.GREETING) {
            return staticResponses.greetingAnswer();
        }
        if (plan.intent() == AssistantIntent.ASSISTANT_IDENTITY) {
            return staticResponses.identityAnswer();
        }
        if (plan.intent() == AssistantIntent.ASSISTANT_CAPABILITIES) {
            return staticResponses.capabilitiesAnswer();
        }
        if (plan.intent() == AssistantIntent.UNCLEAR_REQUEST) {
            return staticResponses.unclearRequestAnswer();
        }
        if (plan.intent() == AssistantIntent.UNSUPPORTED) {
            return staticResponses.unsupportedAnswer();
        }
        if (plan.intent() == AssistantIntent.BUSINESS_PARTNER_EXISTENCE) {
            return existenceAnswer(plan, retrieval.results());
        }

        List<AssistantSearchResult> records = retrieval.results();
        if (records.isEmpty()) {
            return emptyAnswer(plan);
        }

        String heading = headingFor(plan, records.size());
        String recordLines = records.stream()
                .limit(MAX_RECORDS_IN_TEXT)
                .map(this::recordLine)
                .collect(Collectors.joining("\n"));
        String truncationNotice = records.size() > MAX_RECORDS_IN_TEXT
                ? "\n\nOnly the first " + MAX_RECORDS_IN_TEXT + " records are shown here. Use the references to inspect the rest."
                : "";
        String toolSummary = toolSummary(retrieval.toolResults());

        return """
                %s

                Key records:
                %s%s

                %sSuggested next manual action:
                Open the referenced CRM records and review the relevant data manually.
                """.formatted(heading, recordLines, truncationNotice, toolSummary).trim();
    }

    private String existenceAnswer(AssistantPlan plan, List<AssistantSearchResult> records) {
        String target = targetLabel(plan);
        if (records.isEmpty()) {
            return """
                    No. I did not find an active %s matching "%s".

                    Suggested next manual action:
                    Check spelling, archived records, or try searching by code, email, or contact person.
                    """.formatted(kindLabel(plan), target).trim();
        }

        String recordLines = records.stream()
                .limit(MAX_RECORDS_IN_TEXT)
                .map(this::recordLine)
                .collect(Collectors.joining("\n"));
        String plural = records.size() == 1 ? "" : "s";
        String truncationNotice = records.size() > MAX_RECORDS_IN_TEXT
                ? "\n\nOnly the first " + MAX_RECORDS_IN_TEXT + " records are shown here. Use the references to inspect the rest."
                : "";

        return """
                Yes. I found %d active %s%s matching "%s".

                Key records:
                %s%s

                Suggested next manual action:
                Open the referenced CRM record%s to verify the details.
                """.formatted(records.size(), kindLabel(plan), plural, target, recordLines, truncationNotice, plural).trim();
    }

    private String emptyAnswer(AssistantPlan plan) {
        return switch (plan.intent()) {
            case LEADS_MISSING_CONTACTS -> """
                    I did not find active leads without contact persons.

                    Suggested next manual action:
                    Check archived leads or verify that the relevant leads exist if you expected results.
                    """.trim();
            case LEADS_NEED_FOLLOW_UP -> """
                    I did not find active leads that currently need follow-up based on the configured stale-lead criteria.

                    Suggested next manual action:
                    Review the lead follow-up threshold if you expected older open leads to appear.
                    """.trim();
            case BUSINESS_PARTNER_SEARCH, BUSINESS_PARTNER_DETAILS -> """
                    I did not find matching active CRM records for "%s".

                    Suggested next manual action:
                    Try a more specific name, code, email, contact person, or marketing source.
                    """.formatted(targetLabel(plan)).trim();
            default -> """
                    I did not find matching active CRM data for this request.

                    Suggested next manual action:
                    Check whether matching records are archived or whether the CRM data exists.
                    """.trim();
        };
    }

    private String headingFor(AssistantPlan plan, int recordCount) {
        String noun = recordCount == 1 ? "record" : "records";
        return switch (plan.intent()) {
            case LEADS_MISSING_CONTACTS -> "I found " + recordCount + " active lead" + plural(recordCount) + " without contact persons.";
            case LEADS_NEED_FOLLOW_UP -> "I found " + recordCount + " lead" + plural(recordCount) + " that may need follow-up.";
            case MARKETING_PERFORMANCE -> "I found marketing-source performance data for " + recordCount + " source" + plural(recordCount) + ".";
            case CONTACT_COVERAGE -> "I found contact-coverage data for " + recordCount + " CRM categor" + (recordCount == 1 ? "y" : "ies") + ".";
            case RECENT_RECORDS -> "I found " + recordCount + " recently created CRM " + noun + ".";
            case STATUS_BREAKDOWN -> "I found " + recordCount + " CRM status breakdown " + noun + ".";
            case LEAD_PIPELINE -> "I found lead-pipeline data for " + recordCount + " status group" + plural(recordCount) + ".";
            case BUSINESS_PARTNER_SEARCH -> "I found " + recordCount + " matching active CRM " + noun + " for \"" + targetLabel(plan) + "\".";
            case BUSINESS_PARTNER_DETAILS -> "I found details for " + recordCount + " active CRM " + noun + " matching \"" + targetLabel(plan) + "\".";
            default -> "I found " + recordCount + " matching CRM " + noun + ".";
        };
    }

    private String recordLine(AssistantSearchResult result) {
        AssistantRecordReference reference = result.reference();
        Map<String, String> fields = result.fields();
        StringBuilder builder = new StringBuilder("- ").append(reference.label());

        appendField(builder, fields, "Kind", "kind");
        appendField(builder, fields, "Status", "status");
        appendField(builder, fields, "Marketing source", "source");
        appendField(builder, fields, "Primary email", "email");
        appendField(builder, fields, "Primary contact", "primary contact");
        appendField(builder, fields, "Contact persons", "contact persons");
        appendField(builder, fields, "Lead count", "lead count");
        appendField(builder, fields, "Qualified leads", "qualified leads");
        appendField(builder, fields, "Open leads", "open leads");
        appendField(builder, fields, "Records without contact persons", "without contacts");
        appendField(builder, fields, "Record count", "record count");
        appendField(builder, fields, "Active records", "active records");
        appendField(builder, fields, "Qualified records", "qualified records");
        appendField(builder, fields, "Created in last 30 days", "created last 30 days");
        appendField(builder, fields, "Stale leads", "stale leads");
        appendField(builder, fields, "Leads without contact persons", "leads without contact persons");
        appendField(builder, fields, "Last updated", "last updated");

        return builder.toString();
    }

    private void appendField(StringBuilder builder, Map<String, String> fields, String key, String label) {
        String value = fields.get(key);
        if (value != null && !value.isBlank()) {
            builder.append(builder.indexOf(" — ") < 0 ? " — " : ", ")
                    .append(label)
                    .append(": ")
                    .append(value);
        }
    }

    private String toolSummary(List<AssistantToolResult> toolResults) {
        String summary = toolResults.stream()
                .map(AssistantToolResult::summary)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .collect(Collectors.joining(" "));
        return summary.isBlank() ? "" : "Tool summary: " + summary + "\n\n";
    }

    private String plural(int count) {
        return count == 1 ? "" : "s";
    }

    private String kindLabel(AssistantPlan plan) {
        if (plan.kindCode().isBlank()) {
            return "CRM record";
        }
        return plan.kindCode().toLowerCase(Locale.ROOT).replace('_', ' ');
    }

    private String targetLabel(AssistantPlan plan) {
        if (!plan.searchTerm().isBlank()) {
            return plan.searchTerm();
        }
        return plan.normalizedQuery().isBlank() ? "the requested value" : plan.normalizedQuery();
    }
}

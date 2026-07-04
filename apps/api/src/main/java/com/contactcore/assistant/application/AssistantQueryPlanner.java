// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import com.contactcore.assistant.application.nlu.AssistantConcept;
import com.contactcore.assistant.application.nlu.AssistantMessageNormalizer;
import com.contactcore.assistant.application.nlu.AssistantNluRequest;
import com.contactcore.assistant.application.nlu.AssistantNluRouter;
import com.contactcore.assistant.application.nlu.AssistantNormalizedMessage;
import com.contactcore.assistant.application.nlu.CompositeAssistantNluRouter;
import com.contactcore.assistant.application.planning.AssistantIntent;
import com.contactcore.assistant.application.planning.AssistantIntentClassifier;
import com.contactcore.assistant.application.planning.AssistantToolPlanValidator;
import com.contactcore.assistant.retrieval.AssistantPlan;
import com.contactcore.assistant.retrieval.AssistantRetrievalType;
import com.contactcore.assistant.tool.AssistantToolCall;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssistantQueryPlanner {
    private static final int MAX_TOOL_CALLS = 3;

    private final AssistantProperties properties;
    private final AssistantEntityExtractor entityExtractor;
    private final AssistantIntentClassifier intentClassifier;
    private final AssistantToolPlanValidator planValidator;
    private final AssistantNluRouter nluRouter;

    public AssistantQueryPlanner(AssistantProperties properties,
                                 AssistantEntityExtractor entityExtractor,
                                 AssistantIntentClassifier intentClassifier,
                                 AssistantToolPlanValidator planValidator) {
        this(properties, entityExtractor, intentClassifier, planValidator, new CompositeAssistantNluRouter(new AssistantMessageNormalizer()));
    }

    @Autowired
    public AssistantQueryPlanner(AssistantProperties properties,
                                 AssistantEntityExtractor entityExtractor,
                                 AssistantIntentClassifier intentClassifier,
                                 AssistantToolPlanValidator planValidator,
                                 AssistantNluRouter nluRouter) {
        this.properties = properties;
        this.entityExtractor = entityExtractor;
        this.intentClassifier = intentClassifier;
        this.planValidator = planValidator;
        this.nluRouter = nluRouter;
    }

    public AssistantQueryPlanner(AssistantProperties properties,
                                 AssistantEntityExtractor entityExtractor,
                                 AssistantIntentClassifier intentClassifier,
                                 AssistantToolPlanValidator planValidator,
                                 AssistantMessageNormalizer messageNormalizer) {
        this(properties, entityExtractor, intentClassifier, planValidator, new CompositeAssistantNluRouter(messageNormalizer));
    }

    public AssistantPlan plan(String normalizedMessage) {
        AssistantNormalizedMessage normalizedInput = nluRouter.route(new AssistantNluRequest(normalizedMessage)).normalizedMessage();
        if (normalizedInput.hasConflictingBusinessPartnerTypes()) {
            return ambiguousBusinessPartnerTypePlan(normalizedInput);
        }
        String normalized = normalizedInput.canonicalText();
        String lower = normalized.toLowerCase(Locale.ROOT);
        AssistantEntityExtractor.AssistantCriteria extractedCriteria = entityExtractor.extract(normalized, normalizedInput.originalText());
        AssistantIntent intent = intentClassifier.classify(normalized, extractedCriteria);
        AssistantEntityExtractor.AssistantCriteria criteria = criteriaForIntent(intent, extractedCriteria);
        List<AssistantToolCall> toolCalls = selectToolCalls(intent, lower, criteria);
        planValidator.validate(intent, toolCalls);
        AssistantRetrievalType type = classify(intent, toolCalls);
        return new AssistantPlan(
                type,
                intent,
                normalized,
                criteria.searchTerm(),
                criteria.kindCode(),
                criteria.source(),
                properties.maxResults(),
                toolCalls,
                userIntent(intent, criteria)
        );
    }


    private AssistantPlan ambiguousBusinessPartnerTypePlan(AssistantNormalizedMessage normalizedInput) {
        String conflictingTypes = normalizedInput.detectedBusinessPartnerTypes().stream()
                .map(AssistantConcept::name)
                .sorted()
                .collect(Collectors.joining(","));
        return new AssistantPlan(
                AssistantRetrievalType.ASSISTANT_HELP,
                AssistantIntent.UNCLEAR_REQUEST,
                normalizedInput.canonicalText(),
                "",
                "",
                "",
                properties.maxResults(),
                List.of(),
                "UNCLEAR_REQUEST conflicting business partner types [" + conflictingTypes + "]"
        );
    }

    private AssistantEntityExtractor.AssistantCriteria criteriaForIntent(AssistantIntent intent,
                                                                        AssistantEntityExtractor.AssistantCriteria criteria) {
        if (intent == AssistantIntent.LEADS_MISSING_CONTACTS) {
            return new AssistantEntityExtractor.AssistantCriteria("", "", criteria.source(), criteria.staleDays());
        }
        return criteria;
    }

    private List<AssistantToolCall> selectToolCalls(AssistantIntent intent,
                                                    String lower,
                                                    AssistantEntityExtractor.AssistantCriteria criteria) {
        LinkedHashMap<String, AssistantToolCall> calls = new LinkedHashMap<>();

        switch (intent) {
            case GREETING, ASSISTANT_IDENTITY, ASSISTANT_CAPABILITIES, UNCLEAR_REQUEST, UNSUPPORTED -> {
                // Safe no-tool intents. They are answered deterministically and must not query CRM data.
            }
            case BUSINESS_PARTNER_EXISTENCE, BUSINESS_PARTNER_SEARCH ->
                    add(calls, AssistantToolCall.of("crm.searchRecords", searchArguments(criteria, normalizedSearchFallback(lower))));
            case BUSINESS_PARTNER_DETAILS ->
                    add(calls, AssistantToolCall.of("crm.getBusinessPartnerDetails", searchArguments(criteria, normalizedSearchFallback(lower))));
            case LEADS_MISSING_CONTACTS ->
                    add(calls, AssistantToolCall.of("crm.listLeadsWithoutContactPersons", commonArguments(criteria)));
            case LEADS_NEED_FOLLOW_UP -> {
                Map<String, Object> arguments = commonArguments(criteria);
                arguments.put("staleDays", criteria.staleDays());
                add(calls, AssistantToolCall.of("crm.listStaleLeads", arguments));
            }
            case MARKETING_PERFORMANCE -> add(calls, AssistantToolCall.of("crm.getMarketingSourcePerformance"));
            case RECENT_RECORDS -> add(calls, AssistantToolCall.of("crm.getRecentRecords", commonArguments(criteria)));
            case STATUS_BREAKDOWN -> add(calls, AssistantToolCall.of("crm.getStatusBreakdown"));
            case CONTACT_COVERAGE -> add(calls, AssistantToolCall.of("crm.getContactCoverage"));
            case LEAD_PIPELINE -> add(calls, AssistantToolCall.of("crm.getLeadPipeline"));
            case CRM_SUMMARY -> {
                add(calls, AssistantToolCall.of("crm.getCrmSummary"));
                add(calls, AssistantToolCall.of("crm.getStatusBreakdown"));
                add(calls, AssistantToolCall.of("crm.getContactCoverage"));
            }
        }

        return calls.values().stream().limit(MAX_TOOL_CALLS).toList();
    }

    private void add(LinkedHashMap<String, AssistantToolCall> calls, AssistantToolCall call) {
        calls.putIfAbsent(call.toolName() + call.arguments().toString(), call);
    }

    private Map<String, Object> commonArguments(AssistantEntityExtractor.AssistantCriteria criteria) {
        Map<String, Object> arguments = new LinkedHashMap<>();
        if (!criteria.kindCode().isBlank()) {
            arguments.put("kindCode", criteria.kindCode());
        }
        if (!criteria.source().isBlank()) {
            arguments.put("source", criteria.source());
        }
        return arguments;
    }

    private Map<String, Object> searchArguments(AssistantEntityExtractor.AssistantCriteria criteria, String fallback) {
        Map<String, Object> arguments = commonArguments(criteria);
        String query = criteria.searchTerm().isBlank() ? fallback : criteria.searchTerm();
        arguments.put("query", query);
        return arguments;
    }

    private AssistantRetrievalType classify(AssistantIntent intent, List<AssistantToolCall> calls) {
        if (intent == AssistantIntent.GREETING || intent == AssistantIntent.ASSISTANT_IDENTITY || intent == AssistantIntent.ASSISTANT_CAPABILITIES || intent == AssistantIntent.UNCLEAR_REQUEST || intent == AssistantIntent.UNSUPPORTED) {
            return AssistantRetrievalType.ASSISTANT_HELP;
        }
        if (intent == AssistantIntent.CRM_SUMMARY || calls.size() > 1) {
            return AssistantRetrievalType.MULTI_TOOL;
        }
        if (calls.isEmpty()) {
            return AssistantRetrievalType.CRM_SUMMARY;
        }
        return switch (calls.getFirst().toolName()) {
            case "crm.searchRecords" -> AssistantRetrievalType.CRM_SEARCH;
            case "crm.getBusinessPartnerDetails" -> AssistantRetrievalType.BUSINESS_PARTNER_DETAILS;
            case "crm.listStaleLeads" -> AssistantRetrievalType.STALE_LEADS;
            case "crm.listLeadsWithoutContactPersons" -> AssistantRetrievalType.LEADS_WITHOUT_CONTACTS;
            case "crm.getMarketingSourcePerformance" -> AssistantRetrievalType.MARKETING_SOURCE_ANALYTICS;
            case "crm.getContactCoverage" -> AssistantRetrievalType.CONTACT_COVERAGE;
            case "crm.getRecentRecords" -> AssistantRetrievalType.RECENT_RECORDS;
            case "crm.getStatusBreakdown" -> AssistantRetrievalType.STATUS_BREAKDOWN;
            case "crm.getLeadPipeline" -> AssistantRetrievalType.LEAD_PIPELINE;
            default -> AssistantRetrievalType.CRM_SUMMARY;
        };
    }

    private String userIntent(AssistantIntent intent, AssistantEntityExtractor.AssistantCriteria criteria) {
        String target = criteria.searchTerm().isBlank() ? "" : " for " + criteria.searchTerm();
        String kind = criteria.kindCode().isBlank() ? "" : " [" + criteria.kindCode() + "]";
        return intent.name() + target + kind;
    }

    private String normalizedSearchFallback(String lower) {
        return lower
                .replaceAll("(?i)\\b(do we have|is there|are there|does|exist|exists|find|search|records?|related to|about|show me|show|details?|crm|please|with name|named|called)\\b", "")
                .replaceAll("(?i)\\b(customers?|suppliers?|leads?|business partners?)\\b", "")
                .replaceAll("[?.!,;:]+", " ")
                .trim();
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class AssistantEntityExtractor {
    private static final int MAX_TERM_LENGTH = 120;
    private static final Pattern QUOTED_TEXT = Pattern.compile("[\\\"'“”‘’]([^\\\"'“”‘’]{2,120})[\\\"'“”‘’]");
    private static final Pattern DAYS_PATTERN = Pattern.compile("(?i)(?:older than|more than|over|since)\\s+(\\d{1,3})\\s+days?");
    private static final Pattern EXPLICIT_NAME_PATTERN = Pattern.compile(
            "(?i)\\b(?:with\\s+name|name|named|called)\\s+(.{2,120})$"
    );
    private static final Pattern EXISTENCE_LOOKUP_PATTERN = Pattern.compile(
            "(?i)\\b(?:do\\s+we\\s+have|is\\s+there|are\\s+there|exists?)\\s+" +
                    "(?:an?\\s+|any\\s+)?(?:active\\s+)?(?:customers?|suppliers?|leads?|business\\s+partners?|records?)?\\s*" +
                    "(?:with\\s+name\\s+|named\\s+|called\\s+)?(.{2,120})$"
    );
    private static final Pattern DOES_ENTITY_EXIST_PATTERN = Pattern.compile(
            "(?i)\\bdoes\\s+(?:an?\\s+)?(?:customers?|suppliers?|leads?|business\\s+partners?|records?)\\s+(.{2,120}?)\\s+exist$"
    );

    public AssistantCriteria extract(String message) {
        return extract(message, message);
    }

    public AssistantCriteria extract(String semanticMessage, String sourceMessage) {
        String normalized = semanticMessage == null ? "" : semanticMessage.trim();
        String source = sourceMessage == null ? "" : sourceMessage.trim();
        String lower = normalized.toLowerCase(Locale.ROOT);
        return new AssistantCriteria(
                extractSearchTerm(source, normalized),
                extractKind(lower),
                extractSource(normalized),
                extractStaleDays(lower).orElse(14)
        );
    }

    private String extractSearchTerm(String sourceMessage, String semanticMessage) {
        String sourceCandidate = extractSearchTermCandidate(sourceMessage, true);
        if (!sourceCandidate.isBlank()) {
            return sourceCandidate;
        }
        return extractSearchTermCandidate(semanticMessage, false);
    }

    private String extractSearchTermCandidate(String message, boolean preserveExplicitSourceNames) {
        Matcher quotedMatcher = QUOTED_TEXT.matcher(message);
        if (quotedMatcher.find()) {
            return cleanEntityName(quotedMatcher.group(1));
        }

        Optional<String> explicitName = firstMatch(message, EXPLICIT_NAME_PATTERN, false);
        if (explicitName.isPresent()) {
            return explicitName.get();
        }

        Optional<String> existenceLookup = firstMatch(message, EXISTENCE_LOOKUP_PATTERN, false);
        if (existenceLookup.isPresent()) {
            return existenceLookup.get();
        }

        Optional<String> doesExistLookup = firstMatch(message, DOES_ENTITY_EXIST_PATTERN, false);
        if (doesExistLookup.isPresent()) {
            return doesExistLookup.get();
        }

        if (preserveExplicitSourceNames) {
            return "";
        }

        String lower = message.toLowerCase(Locale.ROOT);
        String[] markers = {
                "related to", "summarize customer", "summarize supplier", "summarize lead",
                "show me", "show", "list", "for customer", "for supplier", "for lead", "about", "find", "search", "for"
        };
        for (String marker : markers) {
            int index = lower.indexOf(marker);
            if (index >= 0) {
                String tail = message.substring(index + marker.length());
                String cleaned = cleanGenericSearchTerm(tail);
                if (!cleaned.isBlank()) {
                    return cleaned;
                }
            }
        }
        return "";
    }

    private Optional<String> firstMatch(String message, Pattern pattern, boolean stripLeadingEntityWords) {
        Matcher matcher = pattern.matcher(message);
        if (!matcher.find()) {
            return Optional.empty();
        }
        String value = stripLeadingEntityWords
                ? cleanGenericSearchTerm(matcher.group(1))
                : cleanEntityName(matcher.group(1));
        return value.isBlank() ? Optional.empty() : Optional.of(value);
    }

    private String extractKind(String lower) {
        if (containsAny(lower, "supplier", "suppliers")) {
            return "SUPPLIER";
        }
        if (containsAny(lower, "customer", "customers")) {
            return "CUSTOMER";
        }
        if (containsAny(lower, "lead", "leads")) {
            return "LEAD";
        }
        return "";
    }

    private String extractSource(String message) {
        String lower = message.toLowerCase(Locale.ROOT);
        String[] markers = {"marketing source", "lead source", "source", "from"};
        for (String marker : markers) {
            int index = lower.indexOf(marker);
            if (index >= 0) {
                String tail = message.substring(index + marker.length());
                String cleaned = cleanGenericSearchTerm(tail);
                if (!cleaned.isBlank() && !isGenericSourceWord(cleaned)) {
                    return cleaned;
                }
            }
        }
        return "";
    }

    private Optional<Integer> extractStaleDays(String lower) {
        Matcher matcher = DAYS_PATTERN.matcher(lower);
        if (!matcher.find()) {
            return Optional.empty();
        }
        try {
            int days = Integer.parseInt(matcher.group(1));
            return Optional.of(Math.clamp(days, 1, 365));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private boolean isGenericSourceWord(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        return lower.equals("performance") || lower.equals("analytics") || lower.equals("report") || lower.equals("reports");
    }

    private String cleanEntityName(String value) {
        return cleanTerm(value, false);
    }

    private String cleanGenericSearchTerm(String value) {
        return cleanTerm(value, true);
    }

    private String cleanTerm(String value, boolean stripLeadingEntityWords) {
        if (value == null) {
            return "";
        }
        String cleaned = value
                .replaceAll("(?i)^(with\\s+name|name|named|called)\\s+", "")
                .replaceAll("(?i)\\b(with|without|that|who|which|need|needs|follow[- ]?up|missing|contact persons?|contacts?|older than.*)$", "")
                .replaceAll("[?.!,;:]+$", "")
                .trim();

        if (stripLeadingEntityWords) {
            cleaned = cleaned.replaceAll("(?i)^(records?|crm|business partners?|customers?|suppliers?|leads?)\\s+", "").trim();
        }

        return cleaned.length() <= MAX_TERM_LENGTH ? cleaned : cleaned.substring(0, MAX_TERM_LENGTH).trim();
    }

    public record AssistantCriteria(
            String searchTerm,
            String kindCode,
            String source,
            int staleDays
    ) {}
}

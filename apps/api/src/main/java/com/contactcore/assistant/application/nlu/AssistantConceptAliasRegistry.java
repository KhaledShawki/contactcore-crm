// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class AssistantConceptAliasRegistry {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private final List<AliasRule> rules;
    private final Pattern ignoredTokenPattern;

    public AssistantConceptAliasRegistry() {
        this(AssistantNluCatalogLoader.loadDefaultCatalog());
    }

    public AssistantConceptAliasRegistry(AssistantNluCatalogLoader catalogLoader) {
        this(catalogLoader.load());
    }

    public AssistantConceptAliasRegistry(AssistantNluCatalog catalog) {
        AssistantNluCatalog validatedCatalog = new AssistantNluCatalogValidator().validate(catalog);
        this.rules = rules(validatedCatalog);
        this.ignoredTokenPattern = ignoredTokenPattern(validatedCatalog.ignoredTokens());
    }

    public String toCanonicalText(String message) {
        return normalize(message).canonicalText();
    }

    public AssistantAliasNormalizationResult normalize(String message) {
        String original = message == null ? "" : message.trim();
        if (original.isBlank()) {
            return new AssistantAliasNormalizationResult("", "", List.of());
        }

        List<AssistantConceptAliasMatch> matches = nonOverlappingConceptMatches(original);
        String canonical = original;
        for (AliasRule rule : rules) {
            canonical = rule.pattern().matcher(canonical).replaceAll(Matcher.quoteReplacement(rule.replacement()));
        }
        canonical = ignoredTokenPattern.matcher(canonical).replaceAll(" ");
        canonical = WHITESPACE.matcher(canonical).replaceAll(" ").trim();
        return new AssistantAliasNormalizationResult(original, canonical, matches);
    }

    private List<AssistantConceptAliasMatch> nonOverlappingConceptMatches(String original) {
        List<AssistantConceptAliasMatch> accepted = new ArrayList<>();
        for (AliasRule rule : rules) {
            if (!rule.conceptBacked()) {
                continue;
            }
            Matcher matcher = rule.pattern().matcher(original);
            while (matcher.find()) {
                AssistantConceptAliasMatch candidate = new AssistantConceptAliasMatch(
                        rule.concept(),
                        matcher.group(),
                        rule.replacement(),
                        matcher.start(),
                        matcher.end()
                );
                if (accepted.stream().noneMatch(candidate::overlaps)) {
                    accepted.add(candidate);
                }
            }
        }
        return accepted.stream()
                .sorted(Comparator.comparingInt(AssistantConceptAliasMatch::start))
                .toList();
    }

    private static List<AliasRule> rules(AssistantNluCatalog catalog) {
        List<AliasRule> aliases = new ArrayList<>();
        for (AssistantNluAliasEntry entry : catalog.aliases()) {
            for (String phrase : entry.phrases()) {
                aliases.add(AliasRule.of(entry.concept(), phrase, entry.canonicalText()));
            }
        }
        return aliases.stream()
                .sorted(Comparator.comparingInt(AliasRule::specificity).reversed())
                .toList();
    }

    private static Pattern ignoredTokenPattern(List<String> ignoredTokens) {
        if (ignoredTokens == null || ignoredTokens.isEmpty()) {
            return Pattern.compile("(?!)");
        }
        String expression = ignoredTokens.stream()
                .filter(token -> token != null && !token.isBlank())
                .map(String::trim)
                .sorted(Comparator.comparingInt(String::length).reversed())
                .map(Pattern::quote)
                .reduce((left, right) -> left + "|" + right)
                .orElse("(?!)");
        return Pattern.compile("(?iu)(?<![\\p{L}\\p{N}_])(?:" + expression + ")(?![\\p{L}\\p{N}_])");
    }

    private record AliasRule(AssistantConcept concept, String alias, String replacement, Pattern pattern) {
        static AliasRule of(AssistantConcept concept, String alias, String replacement) {
            return new AliasRule(concept, alias, replacement, compile(alias));
        }

        boolean conceptBacked() {
            return concept != null;
        }

        int specificity() {
            return alias.replaceAll("\\s+", "").length() + tokenCount() * 10;
        }

        int tokenCount() {
            return alias.isBlank() ? 0 : alias.trim().split("\\s+").length;
        }

        private static Pattern compile(String alias) {
            String[] tokens = alias.trim().toLowerCase(Locale.ROOT).split("\\s+");
            StringBuilder expression = new StringBuilder();
            for (int index = 0; index < tokens.length; index++) {
                if (index > 0) {
                    expression.append("\\s+");
                }
                expression.append(Pattern.quote(tokens[index]));
            }
            return Pattern.compile("(?iu)(?<![\\p{L}\\p{N}_])" + expression + "(?![\\p{L}\\p{N}_])");
        }
    }
}

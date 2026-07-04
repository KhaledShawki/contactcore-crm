// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.nlu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class AssistantNluCatalogLoader {
    private static final String DEFAULT_CATALOG_RESOURCE = "assistant/nlu/catalog.yml";
    private static final Pattern ALIAS_PROPERTY = Pattern.compile("aliases\\[(\\d+)]\\.(concept|canonical|phrases\\[(\\d+)])");
    private static final Pattern IGNORED_TOKEN_PROPERTY = Pattern.compile("ignoredTokens\\[(\\d+)]");

    private final Resource catalogResource;
    private final AssistantNluCatalogValidator validator;

    public AssistantNluCatalogLoader() {
        this(new ClassPathResource(DEFAULT_CATALOG_RESOURCE), new AssistantNluCatalogValidator());
    }

    public AssistantNluCatalogLoader(AssistantNluCatalogValidator validator) {
        this(new ClassPathResource(DEFAULT_CATALOG_RESOURCE), validator);
    }

    public AssistantNluCatalogLoader(Resource catalogResource, AssistantNluCatalogValidator validator) {
        this.catalogResource = catalogResource;
        this.validator = validator;
    }

    public AssistantNluCatalog load() {
        try {
            Properties properties = loadProperties();
            AssistantNluCatalog catalog = new AssistantNluCatalog(
                    integerProperty(properties, "version", 1),
                    ignoredTokens(properties),
                    aliasEntries(properties)
            );
            return validator.validate(catalog);
        } catch (AssistantNluCatalogException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new AssistantNluCatalogException("Failed to load assistant NLU catalog from " + catalogResource.getDescription() + ".", exception);
        }
    }

    public static AssistantNluCatalog loadDefaultCatalog() {
        return new AssistantNluCatalogLoader().load();
    }

    private Properties loadProperties() {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(catalogResource);
        Properties properties = factory.getObject();
        if (properties == null || properties.isEmpty()) {
            throw new AssistantNluCatalogException("Assistant NLU catalog is empty: " + catalogResource.getDescription());
        }
        return properties;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        String value = properties.getProperty(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    private List<String> ignoredTokens(Properties properties) {
        Map<Integer, String> tokens = new TreeMap<>();
        for (String name : properties.stringPropertyNames()) {
            Matcher matcher = IGNORED_TOKEN_PROPERTY.matcher(name);
            if (matcher.matches()) {
                tokens.put(Integer.parseInt(matcher.group(1)), properties.getProperty(name));
            }
        }
        return List.copyOf(tokens.values());
    }

    private List<AssistantNluAliasEntry> aliasEntries(Properties properties) {
        Map<Integer, MutableAliasEntry> entries = new TreeMap<>();
        for (String name : properties.stringPropertyNames()) {
            Matcher matcher = ALIAS_PROPERTY.matcher(name);
            if (!matcher.matches()) {
                continue;
            }
            int aliasIndex = Integer.parseInt(matcher.group(1));
            MutableAliasEntry entry = entries.computeIfAbsent(aliasIndex, ignored -> new MutableAliasEntry());
            String field = matcher.group(2);
            String value = properties.getProperty(name);
            if ("concept".equals(field)) {
                entry.concept = parseConcept(value);
            } else if ("canonical".equals(field)) {
                entry.canonical = value;
            } else {
                entry.phrases.put(Integer.parseInt(matcher.group(3)), value);
            }
        }
        return entries.values().stream()
                .map(MutableAliasEntry::toAliasEntry)
                .sorted(Comparator.comparing(AssistantNluAliasEntry::canonicalText))
                .toList();
    }

    private AssistantConcept parseConcept(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return AssistantConcept.valueOf(value.trim());
        } catch (IllegalArgumentException exception) {
            throw new AssistantNluCatalogException("Unknown assistant NLU concept '" + value + "'.", exception);
        }
    }

    private static final class MutableAliasEntry {
        private AssistantConcept concept;
        private String canonical = "";
        private final Map<Integer, String> phrases = new TreeMap<>();

        private AssistantNluAliasEntry toAliasEntry() {
            return new AssistantNluAliasEntry(concept, canonical, new ArrayList<>(phrases.values()));
        }
    }
}

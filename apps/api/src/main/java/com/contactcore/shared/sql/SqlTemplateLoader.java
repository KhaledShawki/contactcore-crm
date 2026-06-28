// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.sql;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class SqlTemplateLoader {
    private static final String CLASSPATH_PREFIX = "classpath:";

    private final ResourceLoader resourceLoader;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public SqlTemplateLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String load(String classpathLocation) {
        return cache.computeIfAbsent(classpathLocation, this::readTemplate);
    }

    private String readTemplate(String classpathLocation) {
        Resource resource = resourceLoader.getResource(CLASSPATH_PREFIX + classpathLocation);
        if (!resource.exists()) {
            throw new IllegalStateException("SQL template does not exist: " + classpathLocation);
        }

        try (var inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not read SQL template: " + classpathLocation, ex);
        }
    }
}

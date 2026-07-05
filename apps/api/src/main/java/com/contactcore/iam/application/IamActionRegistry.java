// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamActionCatalog;
import com.contactcore.iam.domain.IamActionDescriptor;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class IamActionRegistry {
    private final Map<IamAction, IamActionDescriptor> actions;
    private final Map<String, List<IamActionDescriptor>> actionsByService;

    public IamActionRegistry(List<IamActionCatalog> catalogs) {
        List<IamActionCatalog> safeCatalogs = catalogs == null ? List.of() : catalogs.stream()
                .map(catalog -> Objects.requireNonNull(catalog, "catalog must not be null"))
                .sorted(Comparator.comparing(IamActionCatalog::service))
                .toList();
        Map<IamAction, IamActionDescriptor> discoveredActions = new LinkedHashMap<>();
        for (IamActionCatalog catalog : safeCatalogs) {
            validateCatalog(catalog);
            for (IamActionDescriptor descriptor : catalog.actions()) {
                IamActionDescriptor previous = discoveredActions.putIfAbsent(descriptor.action(), descriptor);
                if (previous != null) {
                    throw new IllegalStateException("Duplicate IAM action descriptor: " + descriptor.action().value());
                }
            }
        }
        this.actions = Map.copyOf(discoveredActions);
        this.actionsByService = this.actions.values().stream()
                .sorted(Comparator.comparing(IamActionDescriptor::service).thenComparing(IamActionDescriptor::operation))
                .collect(Collectors.groupingBy(
                        IamActionDescriptor::service,
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), List::copyOf)
                ));
    }

    public Collection<IamActionDescriptor> all() {
        return actions.values().stream()
                .sorted(Comparator.comparing(IamActionDescriptor::service).thenComparing(IamActionDescriptor::operation))
                .toList();
    }

    public Optional<IamActionDescriptor> find(IamAction action) {
        return Optional.ofNullable(actions.get(action));
    }

    public boolean contains(IamAction action) {
        return actions.containsKey(action);
    }

    public List<IamActionDescriptor> byService(String service) {
        if (service == null || service.isBlank()) {
            return List.of();
        }
        return actionsByService.getOrDefault(service.trim().toLowerCase(java.util.Locale.ROOT), List.of());
    }

    private void validateCatalog(IamActionCatalog catalog) {
        String service = normalizeService(catalog.service());
        List<IamActionDescriptor> descriptors = catalog.actions();
        if (descriptors == null) {
            throw new IllegalStateException("IAM action catalog returned null actions: " + service);
        }
        Map<IamAction, Long> counts = descriptors.stream()
                .map(descriptor -> Objects.requireNonNull(descriptor, "descriptor must not be null"))
                .peek(descriptor -> {
                    if (!service.equals(descriptor.service())) {
                        throw new IllegalStateException(
                                "IAM action catalog " + service + " cannot register action " + descriptor.action().value()
                        );
                    }
                })
                .map(IamActionDescriptor::action)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .findFirst()
                .ifPresent(entry -> {
                    throw new IllegalStateException("Duplicate IAM action inside catalog: " + entry.getKey().value());
                });
    }

    private String normalizeService(String service) {
        String normalized = Objects.requireNonNull(service, "service must not be null").trim().toLowerCase(java.util.Locale.ROOT);
        if (normalized.isBlank()) {
            throw new IllegalStateException("IAM action catalog service must not be blank");
        }
        return normalized;
    }
}

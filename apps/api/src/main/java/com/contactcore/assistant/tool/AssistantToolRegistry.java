// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.tool;

import com.contactcore.shared.api.InvalidRequestException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AssistantToolRegistry {
    private final Map<String, AssistantTool> toolsByName;

    public AssistantToolRegistry(List<AssistantTool> tools) {
        Map<String, AssistantTool> indexedTools = new LinkedHashMap<>();
        for (AssistantTool tool : tools) {
            AssistantTool previous = indexedTools.putIfAbsent(tool.name(), tool);
            if (previous != null) {
                throw new IllegalStateException("Duplicate assistant tool registered: " + tool.name());
            }
        }
        this.toolsByName = Map.copyOf(indexedTools);
    }

    public AssistantTool require(String name) {
        AssistantTool tool = toolsByName.get(name);
        if (tool == null) {
            throw new InvalidRequestException("Assistant tool is not available: " + name);
        }
        return tool;
    }

    public List<AssistantTool> tools() {
        return toolsByName.values().stream().toList();
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.i18n;

public interface AssistantLanguageDetector {
    AssistantLanguageDetectionResult detect(String message);
}

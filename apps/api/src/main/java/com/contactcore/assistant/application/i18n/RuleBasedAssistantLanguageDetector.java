// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application.i18n;

import com.contactcore.shared.localization.SupportedLocale;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedAssistantLanguageDetector implements AssistantLanguageDetector {
    private static final Set<String> EN_COMMAND_WORDS = Set.of(
            "show", "find", "search", "list", "summarize", "summary", "customer", "supplier", "lead", "business", "partner", "what", "which", "how", "do", "does", "there", "help"
    );
    private static final Set<String> DE_COMMAND_WORDS = Set.of(
            "zeige", "zeig", "finden", "finde", "suche", "such", "liste", "kunden", "kunde", "lieferant", "lieferanten", "geschäftspartner", "geschaeftspartner", "lead", "leads", "zusammenfassung", "hilfe", "was", "welche", "welcher", "wieviele", "gibt", "haben"
    );
    private static final Set<String> AR_COMMAND_WORDS = Set.of(
            "اعرض", "اظهر", "أظهر", "ابحث", "لخص", "ملخص", "عميل", "العميل", "عملاء", "العملاء", "مورد", "المورد", "موردين", "شريك", "تجاري", "محتمل", "المحتمل", "مساعدة"
    );

    @Override
    public AssistantLanguageDetectionResult detect(String message) {
        String normalized = message == null ? "" : message.trim();
        if (normalized.isBlank()) {
            return AssistantLanguageDetectionResult.unknown("blank message");
        }

        int arabicCharacters = countArabicCharacters(normalized);
        int letters = countLetters(normalized);
        if (arabicCharacters >= 2 && letters > 0) {
            double ratio = (double) arabicCharacters / (double) letters;
            return new AssistantLanguageDetectionResult(SupportedLocale.AR, Math.max(0.85d, ratio), "arabic script");
        }

        String lower = normalized.toLowerCase(Locale.ROOT);
        int en = score(lower, EN_COMMAND_WORDS) + commandBoost(lower, "show", "find", "search", "list", "summarize", "what", "which", "how");
        int de = score(lower, DE_COMMAND_WORDS) + commandBoost(lower, "zeige", "zeig", "finde", "suche", "such", "liste", "was", "welche", "welcher");
        int ar = scoreArabic(normalized);

        if (containsGermanSignal(lower)) {
            de += 2;
        }
        if (ar > 0) {
            ar += 3;
        }

        int max = Math.max(en, Math.max(de, ar));
        int second = secondHighest(en, de, ar);
        if (max <= 0 || max == second) {
            return AssistantLanguageDetectionResult.unknown("no dominant assistant language signal");
        }

        SupportedLocale locale = max == ar ? SupportedLocale.AR : max == de ? SupportedLocale.DE : SupportedLocale.EN;
        double confidence = Math.min(0.95d, 0.50d + ((double) (max - second) * 0.15d) + ((double) max * 0.03d));
        return new AssistantLanguageDetectionResult(locale, confidence, "dominant assistant command vocabulary");
    }

    private int score(String lower, Set<String> words) {
        int score = 0;
        for (String word : lower.split("[^\\p{L}\\p{N}_-]+")) {
            if (words.contains(word)) {
                score++;
            }
        }
        return score;
    }

    private int commandBoost(String lower, String... commands) {
        for (String command : commands) {
            if (lower.startsWith(command + " ") || lower.equals(command)) {
                return 3;
            }
        }
        return 0;
    }

    private int scoreArabic(String message) {
        int score = 0;
        for (String token : message.split("\\s+")) {
            if (AR_COMMAND_WORDS.contains(token.trim())) {
                score++;
            }
        }
        return score;
    }

    private boolean containsGermanSignal(String lower) {
        return lower.contains("ä") || lower.contains("ö") || lower.contains("ü") || lower.contains("ß") || lower.contains("geschäft");
    }

    private int secondHighest(int a, int b, int c) {
        int max = Math.max(a, Math.max(b, c));
        int min = Math.min(a, Math.min(b, c));
        return a + b + c - max - min;
    }

    private int countArabicCharacters(String value) {
        int count = 0;
        for (int index = 0; index < value.length(); index++) {
            Character.UnicodeBlock block = Character.UnicodeBlock.of(value.charAt(index));
            if (block == Character.UnicodeBlock.ARABIC || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B) {
                count++;
            }
        }
        return count;
    }

    private int countLetters(String value) {
        int count = 0;
        for (int index = 0; index < value.length(); index++) {
            if (Character.isLetter(value.charAt(index))) {
                count++;
            }
        }
        return count;
    }
}

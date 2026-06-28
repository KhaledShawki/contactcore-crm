// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.security;

final class FileSignature {
    private FileSignature() {}

    static boolean matches(String extension, byte[] content) {
        return switch (extension) {
            case "jpg", "jpeg" -> startsWith(content, 0xFF, 0xD8, 0xFF);
            case "png" -> startsWith(content, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);
            case "webp" -> content.length >= 12
                    && ascii(content, 0, "RIFF")
                    && ascii(content, 8, "WEBP");
            case "pdf" -> ascii(content, 0, "%PDF");
            case "docx", "xlsx" -> startsWith(content, 0x50, 0x4B, 0x03, 0x04)
                    || startsWith(content, 0x50, 0x4B, 0x05, 0x06)
                    || startsWith(content, 0x50, 0x4B, 0x07, 0x08);
            case "csv", "txt" -> looksLikePlainText(content);
            default -> false;
        };
    }

    private static boolean startsWith(byte[] content, int... expected) {
        if (content.length < expected.length) {
            return false;
        }
        for (int index = 0; index < expected.length; index++) {
            if ((content[index] & 0xFF) != expected[index]) {
                return false;
            }
        }
        return true;
    }

    private static boolean ascii(byte[] content, int offset, String expected) {
        if (content.length < offset + expected.length()) {
            return false;
        }
        for (int index = 0; index < expected.length(); index++) {
            if (content[offset + index] != (byte) expected.charAt(index)) {
                return false;
            }
        }
        return true;
    }

    private static boolean looksLikePlainText(byte[] content) {
        int sampleSize = Math.min(content.length, 4096);
        for (int index = 0; index < sampleSize; index++) {
            int value = content[index] & 0xFF;
            boolean allowedControl = value == '\n' || value == '\r' || value == '\t';
            if (value < 0x20 && !allowedControl) {
                return false;
            }
        }
        return true;
    }
}

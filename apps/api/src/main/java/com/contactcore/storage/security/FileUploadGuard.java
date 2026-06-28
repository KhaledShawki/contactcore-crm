// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.security;

import com.contactcore.shared.api.InvalidRequestException;
import com.contactcore.storage.application.ValidatedFileUpload;
import com.contactcore.storage.scanning.FileScanResult;
import com.contactcore.storage.scanning.FileScanner;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadGuard {
    private static final Map<UploadPurpose, Set<String>> ALLOWED_EXTENSIONS = Map.of(
            UploadPurpose.PROFILE_IMAGE, Set.of("jpg", "jpeg", "png", "webp"),
            UploadPurpose.BUSINESS_DOCUMENT, Set.of("pdf", "docx", "xlsx", "csv", "txt", "jpg", "jpeg", "png", "webp")
    );

    private static final Map<UploadPurpose, Set<String>> ALLOWED_CONTENT_TYPES = Map.of(
            UploadPurpose.PROFILE_IMAGE, Set.of("image/jpeg", "image/png", "image/webp"),
            UploadPurpose.BUSINESS_DOCUMENT, Set.of(
                    "application/pdf",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "text/csv",
                    "text/plain",
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            )
    );

    private static final Set<String> DANGEROUS_EXTENSIONS = Set.of(
            "ade", "adp", "app", "apk", "appx", "bat", "cmd", "com", "cpl", "dll", "dmg", "exe", "hta", "jar", "js", "jse",
            "lnk", "msi", "msp", "pif", "ps1", "reg", "scr", "sh", "vbe", "vbs", "wsf", "xll"
    );

    private final FileUploadPolicyProperties properties;
    private final FileScanner scanner;

    public FileUploadGuard(FileUploadPolicyProperties properties, FileScanner scanner) {
        this.properties = properties;
        this.scanner = scanner;
    }

    public ValidatedFileUpload validateAndScan(UploadPurpose purpose, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Uploaded file is empty.");
        }

        byte[] content = file.getBytes();
        long maxBytes = maxBytes(purpose);
        if (content.length > maxBytes) {
            throw new InvalidRequestException("Uploaded file is too large. Maximum allowed size is " + maxBytes + " bytes.");
        }

        String filename = sanitizeFilename(file.getOriginalFilename());
        String extension = extension(filename);
        String contentType = normalizeContentType(file.getContentType());

        if (DANGEROUS_EXTENSIONS.contains(extension)) {
            throw new InvalidRequestException("File type is not allowed.");
        }
        if (!ALLOWED_EXTENSIONS.get(purpose).contains(extension)) {
            throw new InvalidRequestException("Unsupported file extension: ." + extension);
        }
        if (!ALLOWED_CONTENT_TYPES.get(purpose).contains(contentType)) {
            throw new InvalidRequestException("Unsupported content type: " + contentType);
        }
        if (!FileSignature.matches(extension, content)) {
            throw new InvalidRequestException("File content does not match the expected file type.");
        }

        FileScanResult result = scanner.scan(filename, content);
        if (!result.clean()) {
            throw new InvalidRequestException("Upload rejected by malware scanner: " + result.scannerMessage());
        }

        return new ValidatedFileUpload(filename, contentType, content);
    }

    private long maxBytes(UploadPurpose purpose) {
        return purpose == UploadPurpose.PROFILE_IMAGE
                ? properties.maxProfileImageBytes()
                : properties.maxBusinessDocumentBytes();
    }

    private String extension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            throw new InvalidRequestException("Uploaded file must have a supported extension.");
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String normalizeContentType(String contentType) {
        return contentType == null || contentType.isBlank()
                ? "application/octet-stream"
                : contentType.toLowerCase(Locale.ROOT).split(";", 2)[0].trim();
    }

	private String sanitizeFilename(String filename) {
		String fallback = filename == null || filename.isBlank()
				? "upload.bin"
				: filename.trim();

		String leafName = fallback.replace('\\', '/');
		int separatorIndex = leafName.lastIndexOf('/');
		if (separatorIndex >= 0) {
			leafName = leafName.substring(separatorIndex + 1);
		}

		String normalized = Normalizer.normalize(leafName, Normalizer.Form.NFKD)
				.replaceAll("\\p{M}+", "")
				.replaceAll("[^A-Za-z0-9._-]+", "-")
				.replaceAll("-{2,}", "-");

		int extensionIndex = normalized.lastIndexOf('.');
		String stem;
		String extension;

		if (extensionIndex > 0 && extensionIndex < normalized.length() - 1) {
			stem = normalized.substring(0, extensionIndex);
			extension = normalized.substring(extensionIndex + 1);
		} else {
			stem = normalized;
			extension = "";
		}

		stem = trimUnsafeEdges(stem);
		extension = trimUnsafeEdges(extension);

		if (stem.isBlank()) {
			stem = "upload";
		}

		return extension.isBlank()
				? stem
				: stem + "." + extension;
	}

	private String trimUnsafeEdges(String value) {
		return value.replaceAll("^[._-]+|[._-]+$", "");
	}
}

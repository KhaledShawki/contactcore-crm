// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.contactcore.shared.api.InvalidRequestException;
import com.contactcore.storage.application.ValidatedFileUpload;
import com.contactcore.storage.scanning.FileScanResult;
import com.contactcore.storage.scanning.FileScanner;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileUploadGuardTest {
    private static final byte[] PNG_BYTES = new byte[] {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00
    };

    @Test
    void acceptsCleanProfileImageWhenExtensionContentTypeAndSignatureMatch() throws IOException {
        FileUploadGuard guard = new FileUploadGuard(new FileUploadPolicyProperties(1024, 2048), cleanScanner());
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", PNG_BYTES);

        ValidatedFileUpload upload = guard.validateAndScan(UploadPurpose.PROFILE_IMAGE, file);

        assertThat(upload.originalFilename()).isEqualTo("avatar.png");
        assertThat(upload.contentType()).isEqualTo("image/png");
        assertThat(upload.content()).containsExactly(PNG_BYTES);
    }

    @Test
    void rejectsExecutableExtensionsBeforeStorage() {
        FileUploadGuard guard = new FileUploadGuard(new FileUploadPolicyProperties(1024, 2048), cleanScanner());
        MockMultipartFile file = new MockMultipartFile("file", "payload.js", "text/plain", "alert(1)".getBytes());

        assertThatThrownBy(() -> guard.validateAndScan(UploadPurpose.BUSINESS_DOCUMENT, file))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("not allowed");
    }

    @Test
    void rejectsProfileImageWhenSignatureDoesNotMatchExtension() {
        FileUploadGuard guard = new FileUploadGuard(new FileUploadPolicyProperties(1024, 2048), cleanScanner());
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "not-a-png".getBytes());

        assertThatThrownBy(() -> guard.validateAndScan(UploadPurpose.PROFILE_IMAGE, file))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("does not match");
    }

    @Test
    void rejectsFileWhenScannerReportsInfection() {
        FileUploadGuard guard = new FileUploadGuard(
                new FileUploadPolicyProperties(1024, 2048),
                (filename, content) -> FileScanResult.infected("Eicar-Test-Signature FOUND")
        );
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", PNG_BYTES);

        assertThatThrownBy(() -> guard.validateAndScan(UploadPurpose.PROFILE_IMAGE, file))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Upload rejected by malware scanner");
    }

    @Test
    void rejectsEmptyUploadBeforeScanning() {
        CountingScanner scanner = new CountingScanner(FileScanResult.clean("OK"));
        FileUploadGuard guard = new FileUploadGuard(new FileUploadPolicyProperties(1024, 2048), scanner);
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[0]);

        assertThatThrownBy(() -> guard.validateAndScan(UploadPurpose.PROFILE_IMAGE, file))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("empty");
        assertThat(scanner.calls()).isZero();
    }

    @Test
    void rejectsOversizedProfileImageBeforeScanning() {
        CountingScanner scanner = new CountingScanner(FileScanResult.clean("OK"));
        FileUploadGuard guard = new FileUploadGuard(new FileUploadPolicyProperties(4, 2048), scanner);
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", PNG_BYTES);

        assertThatThrownBy(() -> guard.validateAndScan(UploadPurpose.PROFILE_IMAGE, file))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("too large");
        assertThat(scanner.calls()).isZero();
    }

    @Test
    void rejectsUnsupportedContentTypeBeforeScanning() {
        CountingScanner scanner = new CountingScanner(FileScanResult.clean("OK"));
        FileUploadGuard guard = new FileUploadGuard(new FileUploadPolicyProperties(1024, 2048), scanner);
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "application/octet-stream", PNG_BYTES);

        assertThatThrownBy(() -> guard.validateAndScan(UploadPurpose.PROFILE_IMAGE, file))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Unsupported content type");
        assertThat(scanner.calls()).isZero();
    }

    @Test
    void rejectsMissingExtensionBeforeScanning() {
        CountingScanner scanner = new CountingScanner(FileScanResult.clean("OK"));
        FileUploadGuard guard = new FileUploadGuard(new FileUploadPolicyProperties(1024, 2048), scanner);
        MockMultipartFile file = new MockMultipartFile("file", "avatar", "image/png", PNG_BYTES);

        assertThatThrownBy(() -> guard.validateAndScan(UploadPurpose.PROFILE_IMAGE, file))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("supported extension");
        assertThat(scanner.calls()).isZero();
    }

    @Test
    void sanitizesUnsafeOriginalFilenameBeforeStorage() throws IOException {
        FileUploadGuard guard = new FileUploadGuard(new FileUploadPolicyProperties(1024, 2048), cleanScanner());
        MockMultipartFile file = new MockMultipartFile("file", "../A vatar ä.png", "image/png", PNG_BYTES);

        ValidatedFileUpload upload = guard.validateAndScan(UploadPurpose.PROFILE_IMAGE, file);

		assertThat(upload.originalFilename()).isEqualTo("A-vatar-a.png");
    }

    @Test
    void acceptsPdfBusinessDocumentWhenSignatureMatches() throws IOException {
        FileUploadGuard guard = new FileUploadGuard(new FileUploadPolicyProperties(1024, 2048), cleanScanner());
        MockMultipartFile file = new MockMultipartFile("file", "contract.pdf", "application/pdf", "%PDF-1.7".getBytes());

        ValidatedFileUpload upload = guard.validateAndScan(UploadPurpose.BUSINESS_DOCUMENT, file);

        assertThat(upload.originalFilename()).isEqualTo("contract.pdf");
        assertThat(upload.contentType()).isEqualTo("application/pdf");
    }

    private FileScanner cleanScanner() {
        return (filename, content) -> FileScanResult.clean("OK");
    }

    private static final class CountingScanner implements FileScanner {
        private final FileScanResult result;
        private int calls;

        private CountingScanner(FileScanResult result) {
            this.result = result;
        }

        @Override
        public FileScanResult scan(String filename, byte[] content) {
            calls++;
            return result;
        }

        private int calls() {
            return calls;
        }
    }
}

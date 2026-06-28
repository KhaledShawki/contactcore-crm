// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.scanning;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FileScanningConfigTest {
    @Test
    void createsNoOpScannerWhenScanningIsDisabled() {
        FileScanningConfig config = new FileScanningConfig();

        FileScanner scanner = config.fileScanner(new FileScanningProperties(false, "localhost", 3310, 1_000, 1_000, 8_192));

        assertThat(scanner).isInstanceOf(NoOpFileScanner.class);
        assertThat(scanner.scan("file.txt", new byte[] {1}).clean()).isTrue();
    }

    @Test
    void createsClamAvScannerWhenScanningIsEnabled() {
        FileScanningConfig config = new FileScanningConfig();

        FileScanner scanner = config.fileScanner(new FileScanningProperties(true, "localhost", 3310, 1_000, 1_000, 8_192));

        assertThat(scanner).isInstanceOf(ClamAvFileScanner.class);
    }
}

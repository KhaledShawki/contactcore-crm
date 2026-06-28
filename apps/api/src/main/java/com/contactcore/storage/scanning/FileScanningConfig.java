// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.scanning;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileScanningConfig {
    @Bean
    FileScanner fileScanner(FileScanningProperties properties) {
        if (!properties.enabled()) {
            return new NoOpFileScanner();
        }
        return new ClamAvFileScanner(properties);
    }
}

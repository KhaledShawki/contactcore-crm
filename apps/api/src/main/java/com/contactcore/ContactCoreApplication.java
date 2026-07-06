// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore;

import com.contactcore.assistant.application.AssistantProperties;
import com.contactcore.sapb1.config.SapB1Properties;
import com.contactcore.security.application.AdminUserProperties;
import com.contactcore.security.config.JwtProperties;
import com.contactcore.storage.infrastructure.S3StorageProperties;
import com.contactcore.storage.scanning.FileScanningProperties;
import com.contactcore.storage.security.FileUploadPolicyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties({
        AssistantProperties.class,
        AdminUserProperties.class,
        JwtProperties.class,
        SapB1Properties.class,
        S3StorageProperties.class,
        FileScanningProperties.class,
        FileUploadPolicyProperties.class
})
public class ContactCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContactCoreApplication.class, args);
    }
}

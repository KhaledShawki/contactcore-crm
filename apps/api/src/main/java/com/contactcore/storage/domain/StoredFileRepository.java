// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {}

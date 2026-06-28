// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsPrimaryContactUniqueConstraintToClearConflictMessage() {
        HttpServletRequest request = new MockHttpServletRequest("POST", "/api/business-partners/109/contact-persons");
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "duplicate key value violates unique constraint \"uq_bp_contact_person_primary\""
        );

        ResponseEntity<ApiError> response = handler.dataIntegrityViolation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message())
                .isEqualTo("Another contact person is already marked as primary for this business partner. "
                        + "Unselect the current primary contact first, then save this contact as primary.");
    }
}

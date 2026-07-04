// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.contactcore.shared.localization.LocaleContext;
import com.contactcore.shared.localization.LocaleContextResolver;
import com.contactcore.shared.localization.LocalizedMessageService;
import com.contactcore.shared.localization.SupportedLocale;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {
    private final LocaleContextResolver localeContextResolver = mock(LocaleContextResolver.class);
    private final LocalizedMessageService messages = new LocalizedMessageService(new StaticMessageSource());
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(localeContextResolver, messages);

    GlobalExceptionHandlerTest() {
        when(localeContextResolver.resolveForRequest(any(HttpServletRequest.class)))
                .thenReturn(new LocaleContext(SupportedLocale.EN));
    }

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

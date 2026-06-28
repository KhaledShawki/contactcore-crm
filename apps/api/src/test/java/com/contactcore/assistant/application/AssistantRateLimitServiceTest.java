// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.assistant.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.contactcore.shared.api.RateLimitExceededException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class AssistantRateLimitServiceTest {
    @Test
    void rejectsRequestsAboveConfiguredWindowLimit() {
        AssistantRateLimitService service = new AssistantRateLimitService(
                new AssistantProperties(true, "noop", "test", "", "", 5000, 12000, 20, 1),
                Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
        );

        service.check(1L);

        assertThatThrownBy(() -> service.check(1L))
                .isInstanceOf(RateLimitExceededException.class)
                .hasMessageContaining("rate limit");
    }
}

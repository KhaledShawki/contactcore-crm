// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.marketing.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.marketing.api.MarketingSourceWriteRequest;
import org.junit.jupiter.api.Test;

class MarketingSourceNormalizerTest {
    @Test
    void normalizeTrimsCodeNameAndUppercasesCode() {
        NormalizedMarketingSourceInput input = MarketingSourceNormalizer.normalize(
                new MarketingSourceWriteRequest(" linkedin ", " LinkedIn ", 20)
        );

        assertThat(input.code()).isEqualTo("LINKEDIN");
        assertThat(input.name()).isEqualTo("LinkedIn");
        assertThat(input.sortOrder()).isEqualTo(20);
    }

    @Test
    void normalizeUsesStableDefaultSortOrder() {
        NormalizedMarketingSourceInput input = MarketingSourceNormalizer.normalize(
                new MarketingSourceWriteRequest("event", "Event", null)
        );

        assertThat(input.sortOrder()).isEqualTo(100);
    }
}

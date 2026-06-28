// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.marketing.application;

import com.contactcore.marketing.api.MarketingSourceWriteRequest;

record NormalizedMarketingSourceInput(String code, String name, Integer sortOrder) {}

final class MarketingSourceNormalizer {
    private MarketingSourceNormalizer() {}

    static NormalizedMarketingSourceInput normalize(MarketingSourceWriteRequest request) {
        return new NormalizedMarketingSourceInput(
                request.code().trim().toUpperCase(),
                request.name().trim(),
                request.sortOrder() == null ? 100 : request.sortOrder()
        );
    }
}

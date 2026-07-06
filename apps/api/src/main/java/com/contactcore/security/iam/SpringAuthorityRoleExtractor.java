// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.security.iam;

import com.contactcore.iam.application.IamRoleCodeNormalizer;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class SpringAuthorityRoleExtractor {
    private final IamRoleCodeNormalizer normalizer;

    public SpringAuthorityRoleExtractor(IamRoleCodeNormalizer normalizer) {
        this.normalizer = normalizer;
    }

    public List<String> extract(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return List.of();
        }
        return normalizer.normalize(authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
    }
}

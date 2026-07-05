// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.evaluation;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamResource;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class IamMatcher {
    public boolean matchesAction(IamAction policyAction, IamAction requestedAction) {
        String policyValue = policyAction.value();
        String requestedValue = requestedAction.value();
        if ("*".equals(policyValue)) {
            return true;
        }
        return wildcardMatch(policyValue, requestedValue);
    }

    public boolean matchesResource(IamResource policyResource, IamResource requestedResource) {
        String policyValue = policyResource.value();
        String requestedValue = requestedResource.value();
        if ("*".equals(policyValue)) {
            return true;
        }
        return wildcardMatch(policyValue, requestedValue);
    }

    boolean wildcardMatch(String pattern, String value) {
        if (pattern.equals(value)) {
            return true;
        }
        if (!pattern.contains("*")) {
            return false;
        }
        String regex = "^" + Pattern.quote(pattern).replace("*", "\\E.*\\Q") + "$";
        return Pattern.compile(regex).matcher(value).matches();
    }
}

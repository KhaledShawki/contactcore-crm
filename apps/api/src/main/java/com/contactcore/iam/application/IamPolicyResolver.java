// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import com.contactcore.iam.domain.IamPrincipalRef;
import java.util.Collection;

public interface IamPolicyResolver {
    IamResolvedPolicies resolve(IamPrincipalRef principal, Collection<String> roleCodes, String tenantId);
}

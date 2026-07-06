// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.contactcore.iam.domain.IamPolicyDocument;
import org.junit.jupiter.api.Test;

class ManagedIamPoliciesTest {
    @Test
    void exposesSeededManagedPoliciesForCoreRoles() {
        IamPolicyDocument readOnly = ManagedIamPolicies.contactCoreReadOnly("default");
        IamPolicyDocument salesManager = ManagedIamPolicies.salesManager("default");
        IamPolicyDocument connectorAdmin = ManagedIamPolicies.connectorAdministrator("default");
        IamPolicyDocument iamAdmin = ManagedIamPolicies.iamAdministrator("default");

        assertThat(readOnly.statements()).isNotEmpty();
        assertThat(salesManager.statements()).isNotEmpty();
        assertThat(connectorAdmin.statements()).isNotEmpty();
        assertThat(iamAdmin.statements()).isNotEmpty();
    }
}

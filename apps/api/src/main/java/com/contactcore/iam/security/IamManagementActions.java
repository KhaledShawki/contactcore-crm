// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.security;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamActionDescriptor;
import com.contactcore.iam.domain.IamActions;
import java.util.List;

public final class IamManagementActions {
    public static final String SERVICE = "iam";

    public static final IamAction READ_POLICY = IamActions.of(SERVICE, "ReadPolicy");
    public static final IamAction MANAGE_POLICY = IamActions.of(SERVICE, "ManagePolicy");
    public static final IamAction MANAGE_ROLE = IamActions.of(SERVICE, "ManageRole");

    private static final List<IamActionDescriptor> CATALOG = List.of(
            IamActionDescriptor.read(READ_POLICY, "Read IAM policies"),
            IamActionDescriptor.admin(MANAGE_POLICY, "Create, update, attach, or detach IAM policies"),
            IamActionDescriptor.admin(MANAGE_ROLE, "Create, update, or assign IAM roles")
    );

    private IamManagementActions() {}

    public static List<IamActionDescriptor> catalog() {
        return CATALOG;
    }
}

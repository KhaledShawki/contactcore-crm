// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.schema.security;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamActionDescriptor;
import com.contactcore.iam.domain.IamActions;
import java.util.List;

public final class SchemaIamActions {
    public static final String SERVICE = "schema";

    public static final IamAction READ_MANIFEST = IamActions.of(SERVICE, "ReadManifest");

    private static final List<IamActionDescriptor> CATALOG = List.of(
            IamActionDescriptor.read(READ_MANIFEST, "Read the ContactCore UI schema manifest")
    );

    private SchemaIamActions() {}

    public static List<IamActionDescriptor> catalog() {
        return CATALOG;
    }
}

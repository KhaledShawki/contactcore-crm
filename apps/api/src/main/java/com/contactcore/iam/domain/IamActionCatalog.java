// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.domain;

import java.util.List;

public interface IamActionCatalog {
    String service();

    List<IamActionDescriptor> actions();
}

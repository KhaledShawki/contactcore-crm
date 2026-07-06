// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.security;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamActionDescriptor;
import com.contactcore.iam.domain.IamActions;
import java.util.List;

public final class CommercialIamActions {
    public static final String SERVICE = "commercial";

    public static final IamAction LIST_DOCUMENTS = IamActions.of(SERVICE, "ListDocuments");
    public static final IamAction READ_DOCUMENT = IamActions.of(SERVICE, "ReadDocument");
    public static final IamAction EXPORT_DOCUMENTS = IamActions.of(SERVICE, "ExportDocuments");
    public static final IamAction SYNC_DOCUMENTS = IamActions.of(SERVICE, "SyncDocuments");
    public static final IamAction LIST_ITEMS = IamActions.of(SERVICE, "ListItems");
    public static final IamAction READ_ITEM = IamActions.of(SERVICE, "ReadItem");
    public static final IamAction SYNC_ITEMS = IamActions.of(SERVICE, "SyncItems");

    private static final List<IamActionDescriptor> CATALOG = List.of(
            IamActionDescriptor.read(LIST_DOCUMENTS, "List commercial documents"),
            IamActionDescriptor.read(READ_DOCUMENT, "Read commercial document details"),
            IamActionDescriptor.write(EXPORT_DOCUMENTS, "Export commercial documents"),
            IamActionDescriptor.system(SYNC_DOCUMENTS, "Synchronize commercial documents from external systems"),
            IamActionDescriptor.read(LIST_ITEMS, "List commercial items"),
            IamActionDescriptor.read(READ_ITEM, "Read commercial item details"),
            IamActionDescriptor.system(SYNC_ITEMS, "Synchronize commercial items from external systems")
    );

    private CommercialIamActions() {}

    public static List<IamActionDescriptor> catalog() {
        return CATALOG;
    }
}

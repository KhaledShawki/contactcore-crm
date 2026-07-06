// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.security;

import com.contactcore.iam.domain.IamAction;
import com.contactcore.iam.domain.IamActionDescriptor;
import com.contactcore.iam.domain.IamActions;
import java.util.List;

public final class StorageIamActions {
    public static final String SERVICE = "storage";

    public static final IamAction READ_OBJECT = IamActions.of(SERVICE, "ReadObject");
    public static final IamAction UPLOAD_OBJECT = IamActions.of(SERVICE, "UploadObject");
    public static final IamAction DOWNLOAD_OBJECT = IamActions.of(SERVICE, "DownloadObject");
    public static final IamAction PREVIEW_OBJECT = IamActions.of(SERVICE, "PreviewObject");
    public static final IamAction DELETE_OBJECT = IamActions.of(SERVICE, "DeleteObject");

    private static final List<IamActionDescriptor> CATALOG = List.of(
            IamActionDescriptor.read(READ_OBJECT, "Read stored object metadata"),
            IamActionDescriptor.write(UPLOAD_OBJECT, "Upload stored objects"),
            IamActionDescriptor.read(DOWNLOAD_OBJECT, "Download stored object content"),
            IamActionDescriptor.read(PREVIEW_OBJECT, "Preview stored object content"),
            IamActionDescriptor.write(DELETE_OBJECT, "Delete or archive stored objects")
    );

    private StorageIamActions() {}

    public static List<IamActionDescriptor> catalog() {
        return CATALOG;
    }
}

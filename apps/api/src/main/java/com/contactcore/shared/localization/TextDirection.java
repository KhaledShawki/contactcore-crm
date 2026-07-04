// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.shared.localization;

public enum TextDirection {
    LTR,
    RTL;

    public String htmlValue() {
        return name().toLowerCase();
    }
}

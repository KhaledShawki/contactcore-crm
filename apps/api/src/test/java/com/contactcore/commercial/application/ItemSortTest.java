// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ItemSortTest {
    @Test
    void defaultsUnknownSortsToUpdatedDesc() {
        assertThat(ItemSort.from(null)).isEqualTo(ItemSort.UPDATED_DESC);
        assertThat(ItemSort.from("unknown")).isEqualTo(ItemSort.UPDATED_DESC);
    }

    @Test
    void acceptsSnakeCaseAndDashCaseSorts() {
        assertThat(ItemSort.from("code_asc")).isEqualTo(ItemSort.CODE_ASC);
        assertThat(ItemSort.from("name-asc")).isEqualTo(ItemSort.NAME_ASC);
    }
}

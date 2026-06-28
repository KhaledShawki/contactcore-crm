// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.settings.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.contactcore.settings.api.UiSettingsWriteRequest;
import com.contactcore.shared.api.InvalidRequestException;
import org.junit.jupiter.api.Test;

class UiSettingsNormalizerTest {
    @Test
    void normalizesSupportedUiSettings() {
        var input = UiSettingsNormalizer.normalize(new UiSettingsWriteRequest(
                "Ocean", "Large", "Spacious", "Compact", true, true, "/reports"
        ));

        assertThat(input.theme()).isEqualTo("ocean");
        assertThat(input.textSize()).isEqualTo("large");
        assertThat(input.density()).isEqualTo("spacious");
        assertThat(input.sidebarMode()).isEqualTo("compact");
        assertThat(input.defaultLandingPage()).isEqualTo("/reports");
    }

    @Test
    void rejectsUnsupportedThemeAndLandingPage() {
        assertThatThrownBy(() -> UiSettingsNormalizer.normalize(new UiSettingsWriteRequest(
                "neon", "comfortable", "comfortable", "expanded", false, false, "/dashboard"
        ))).isInstanceOf(InvalidRequestException.class).hasMessageContaining("Unsupported theme");

        assertThatThrownBy(() -> UiSettingsNormalizer.normalize(new UiSettingsWriteRequest(
                "ocean", "comfortable", "comfortable", "expanded", false, false, "/admin"
        ))).isInstanceOf(InvalidRequestException.class).hasMessageContaining("Unsupported default landing page");
    }
}

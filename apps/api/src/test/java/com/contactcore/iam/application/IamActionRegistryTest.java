// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.iam.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.contactcore.commercial.security.CommercialIamActionCatalog;
import com.contactcore.commercial.security.CommercialIamActions;
import com.contactcore.iam.domain.IamActionCatalog;
import com.contactcore.iam.domain.IamActionDescriptor;
import com.contactcore.iam.domain.IamActionRiskLevel;
import java.util.List;
import org.junit.jupiter.api.Test;

class IamActionRegistryTest {
    @Test
    void discoversModuleOwnedActionCatalogs() {
        IamActionRegistry registry = new IamActionRegistry(List.of(new CommercialIamActionCatalog()));

        assertThat(registry.find(CommercialIamActions.READ_DOCUMENT))
                .hasValueSatisfying(descriptor -> assertThat(descriptor.riskLevel()).isEqualTo(IamActionRiskLevel.READ));
        assertThat(registry.byService("commercial"))
                .extracting(descriptor -> descriptor.action().value())
                .contains("commercial:ListDocuments", "commercial:ReadDocument", "commercial:SyncDocuments");
    }

    @Test
    void rejectsCatalogsThatRegisterActionsForAnotherService() {
        IamActionCatalog invalidCatalog = new IamActionCatalog() {
            @Override
            public String service() {
                return "crm";
            }

            @Override
            public List<IamActionDescriptor> actions() {
                return List.of(IamActionDescriptor.read(CommercialIamActions.READ_DOCUMENT, "Invalid cross-service action"));
            }
        };

        assertThatThrownBy(() -> new IamActionRegistry(List.of(invalidCatalog)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot register action");
    }

    @Test
    void rejectsDuplicateActionsAcrossCatalogs() {
        IamActionCatalog second = new IamActionCatalog() {
            @Override
            public String service() {
                return "commercial";
            }

            @Override
            public List<IamActionDescriptor> actions() {
                return List.of(IamActionDescriptor.read(CommercialIamActions.READ_DOCUMENT, "Duplicate action"));
            }
        };

        assertThatThrownBy(() -> new IamActionRegistry(List.of(new CommercialIamActionCatalog(), second)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate IAM action descriptor");
    }
}

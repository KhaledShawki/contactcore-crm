// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.dashboard.sap;

import com.contactcore.dashboard.application.CommercialDashboardDocument;
import com.contactcore.dashboard.application.CommercialDashboardDocumentType;
import com.contactcore.dashboard.application.CommercialDashboardLine;
import com.contactcore.sapb1.resource.dashboard.SapB1DashboardDocumentDto;
import com.contactcore.sapb1.resource.dashboard.SapB1DashboardDocumentLineDto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SapDashboardMapper {
    public CommercialDashboardDocument toDocument(CommercialDashboardDocumentType type, SapB1DashboardDocumentDto dto) {
        return new CommercialDashboardDocument(
                type,
                externalId(dto),
                dto.cardCode(),
                dto.cardName(),
                dto.docDate(),
                dto.docDueDate(),
                safe(dto.docTotal()),
                safe(dto.paidToDate()),
                dto.docCurrency(),
                isOpen(dto),
                lines(dto.documentLines())
        );
    }

    private List<CommercialDashboardLine> lines(List<SapB1DashboardDocumentLineDto> lines) {
        return lines.stream()
                .map(line -> new CommercialDashboardLine(
                        line.itemCode(),
                        line.itemDescription(),
                        safe(line.quantity()),
                        safe(line.lineTotal())
                ))
                .toList();
    }

    private String externalId(SapB1DashboardDocumentDto dto) {
        if (dto.docEntry() != null) {
            return dto.docEntry().toString();
        }
        return dto.docNum() == null ? "" : dto.docNum().toString();
    }

    private boolean isOpen(SapB1DashboardDocumentDto dto) {
        return "bost_Open".equalsIgnoreCase(dto.documentStatus()) || safe(dto.docTotal()).subtract(safe(dto.paidToDate())).signum() > 0;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.commercial.application;

import com.contactcore.commercial.domain.CommercialDocument;
import com.contactcore.commercial.domain.CommercialDocumentLine;
import com.contactcore.commercial.domain.CommercialDocumentRepository;
import com.contactcore.commercial.domain.Item;
import com.contactcore.commercial.domain.ItemRepository;
import com.contactcore.crm.domain.BusinessPartner;
import com.contactcore.crm.domain.BusinessPartnerRepository;
import com.contactcore.shared.api.InvalidRequestException;
import com.contactcore.shared.api.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommercialProjectionService {
    private final ItemRepository items;
    private final CommercialDocumentRepository documents;
    private final BusinessPartnerRepository businessPartners;

    public CommercialProjectionService(ItemRepository items, CommercialDocumentRepository documents,
                                       BusinessPartnerRepository businessPartners) {
        this.items = items;
        this.documents = documents;
        this.businessPartners = businessPartners;
    }

    @Transactional
    public Item projectItem(CommercialItemProjectionCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        Item item = items.findActiveBySourceIdentity(command.sourceSystem(), command.sourceTenantId(), command.externalId())
                .orElseGet(() -> new Item(command.sourceSystem(), command.sourceTenantId(), command.externalId(), command.itemCode(), command.name()));
        item.refreshSourceIdentity(command.sourceSystem(), command.sourceTenantId(), command.externalId());
        item.refreshCore(command.itemCode(), command.name());
        item.refreshDetails(command.description(), command.itemGroup(), command.unitOfMeasure(), command.active(), command.lastSyncedAt());
        return items.save(item);
    }

    @Transactional
    public CommercialDocument projectDocument(CommercialDocumentProjectionCommand command, List<CommercialDocumentLineProjectionCommand> lineCommands) {
        Objects.requireNonNull(command, "command must not be null");
        validateLines(lineCommands);
        CommercialDocument document = documents.findActiveBySourceIdentity(command.sourceSystem(), command.sourceTenantId(), command.externalId())
                .orElseGet(() -> new CommercialDocument(
                        command.sourceSystem(),
                        command.sourceTenantId(),
                        command.externalId(),
                        command.externalNumber(),
                        command.type(),
                        command.documentDate(),
                        command.currency()
                ));
        BusinessPartner partner = command.businessPartnerId() == null ? null : businessPartners.findActiveById(command.businessPartnerId())
                .orElseThrow(() -> new NotFoundException("Business partner not found: " + command.businessPartnerId()));
        document.refreshCore(command.externalNumber(), command.documentDate(), command.currency());
        document.refreshHeader(
                command.status(),
                command.sourceStatus(),
                partner,
                command.businessPartnerExternalId(),
                command.businessPartnerCodeSnapshot(),
                command.businessPartnerNameSnapshot(),
                command.dueDate(),
                command.deliveryDate(),
                command.subtotalAmount(),
                command.discountAmount(),
                command.taxAmount(),
                command.totalAmount(),
                command.openAmount(),
                command.lastSyncedAt()
        );
        document.replaceLines(toLines(lineCommands));
        return documents.save(document);
    }

    private List<CommercialDocumentLine> toLines(List<CommercialDocumentLineProjectionCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            return List.of();
        }
        return commands.stream().map(command -> {
            Item item = command.itemId() == null ? null : items.findActiveById(command.itemId())
                    .orElseThrow(() -> new NotFoundException("Item not found: " + command.itemId()));
            CommercialDocumentLine line = new CommercialDocumentLine(command.sourceLineId(), command.lineNumber(), command.currency());
            line.refreshLine(
                    item,
                    command.itemExternalId(),
                    command.itemCodeSnapshot(),
                    command.itemNameSnapshot(),
                    command.description(),
                    command.quantity(),
                    command.openQuantity(),
                    command.unitOfMeasure(),
                    command.unitPrice(),
                    command.discountPercent(),
                    command.taxCodeSnapshot(),
                    command.lineTotal(),
                    command.deliveryDate()
            );
            return line;
        }).toList();
    }

    private void validateLines(List<CommercialDocumentLineProjectionCommand> lineCommands) {
        if (lineCommands == null || lineCommands.isEmpty()) {
            return;
        }
        Set<String> sourceLineIds = new HashSet<>();
        Set<Integer> lineNumbers = new HashSet<>();
        for (CommercialDocumentLineProjectionCommand command : lineCommands) {
            Objects.requireNonNull(command, "line command must not be null");
            if (command.sourceLineId() == null || command.sourceLineId().isBlank()) {
                throw new InvalidRequestException("sourceLineId must not be blank");
            }
            if (command.lineNumber() == null || command.lineNumber() < 0) {
                throw new InvalidRequestException("lineNumber must not be null or negative");
            }
            if (!sourceLineIds.add(command.sourceLineId().trim())) {
                throw new InvalidRequestException("Duplicate sourceLineId in commercial document lines: " + command.sourceLineId());
            }
            if (!lineNumbers.add(command.lineNumber())) {
                throw new InvalidRequestException("Duplicate lineNumber in commercial document lines: " + command.lineNumber());
            }
        }
    }
}

// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.domain;

import com.contactcore.crm.domain.BusinessPartner;
import com.contactcore.crm.domain.DocumentType;
import com.contactcore.shared.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "business_partner_document")
public class BusinessPartnerDocument extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_partner_id", nullable = false)
    private BusinessPartner businessPartner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stored_file_id", nullable = false)
    private StoredFile storedFile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    protected BusinessPartnerDocument() {}

    public BusinessPartnerDocument(BusinessPartner businessPartner, StoredFile storedFile, DocumentType documentType) {
        this.businessPartner = businessPartner;
        this.storedFile = storedFile;
        this.documentType = documentType;
    }

    public BusinessPartner getBusinessPartner() {
        return businessPartner;
    }

    public StoredFile getStoredFile() {
        return storedFile;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }
}

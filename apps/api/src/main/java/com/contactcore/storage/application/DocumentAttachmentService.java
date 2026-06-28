// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.application;

import com.contactcore.crm.application.BusinessPartnerService;
import com.contactcore.crm.application.ReferenceDataResolver;
import com.contactcore.crm.domain.BusinessPartner;
import com.contactcore.crm.domain.DocumentType;
import com.contactcore.shared.api.NotFoundException;
import com.contactcore.storage.api.DocumentAttachmentResponse;
import com.contactcore.storage.domain.BusinessPartnerDocument;
import com.contactcore.storage.security.FileUploadGuard;
import com.contactcore.storage.security.UploadPurpose;
import com.contactcore.storage.domain.BusinessPartnerDocumentRepository;
import com.contactcore.storage.domain.StoredFile;
import com.contactcore.storage.domain.StoredFileRepository;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentAttachmentService {
    private final BusinessPartnerService businessPartners;
    private final ReferenceDataResolver references;
    private final ObjectStorageService objectStorage;
    private final StoredFileRepository storedFiles;
    private final BusinessPartnerDocumentRepository documents;
    private final FileUploadGuard uploadGuard;

    public DocumentAttachmentService(BusinessPartnerService businessPartners, ReferenceDataResolver references,
                                     ObjectStorageService objectStorage, StoredFileRepository storedFiles,
                                     BusinessPartnerDocumentRepository documents, FileUploadGuard uploadGuard) {
        this.businessPartners = businessPartners;
        this.references = references;
        this.objectStorage = objectStorage;
        this.storedFiles = storedFiles;
        this.documents = documents;
        this.uploadGuard = uploadGuard;
    }

    @Transactional(readOnly = true)
    public List<DocumentAttachmentResponse> listBusinessPartnerDocuments(Long businessPartnerId) {
        businessPartners.findActive(businessPartnerId);
        return documents.findActiveByBusinessPartnerId(businessPartnerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DocumentAttachmentResponse uploadBusinessPartnerDocument(Long businessPartnerId, String documentTypeCode, MultipartFile file) throws IOException {
        BusinessPartner partner = businessPartners.findActive(businessPartnerId);
        DocumentType documentType = references.documentType(documentTypeCode == null || documentTypeCode.isBlank() ? "GENERAL" : documentTypeCode.trim().toUpperCase());
        ValidatedFileUpload upload = uploadGuard.validateAndScan(UploadPurpose.BUSINESS_DOCUMENT, file);
        StoredObject storedObject = objectStorage.upload("business-partners/" + businessPartnerId, upload);
        StoredFile storedFile = storedFiles.save(new StoredFile(
                storedObject.objectKey(),
                storedObject.originalFilename(),
                storedObject.contentType(),
                storedObject.sizeBytes()
        ));
        return toResponse(documents.save(new BusinessPartnerDocument(partner, storedFile, documentType)));
    }

    @Transactional
    public void archiveBusinessPartnerDocument(Long documentId) {
        documents.findActiveById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found: " + documentId))
                .archive();
    }

    private DocumentAttachmentResponse toResponse(BusinessPartnerDocument document) {
        StoredFile storedFile = document.getStoredFile();
        return new DocumentAttachmentResponse(
                document.getId(),
                document.getCreatedAt(),
                document.getDocumentType().getCode(),
                document.getDocumentType().getName(),
                storedFile.getOriginalFilename(),
                storedFile.getContentType(),
                storedFile.getSizeBytes(),
                objectStorage.publicUrl(storedFile.getObjectKey())
        );
    }
}

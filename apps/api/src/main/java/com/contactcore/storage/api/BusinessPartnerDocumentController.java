// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.storage.api;

import com.contactcore.storage.application.DocumentAttachmentService;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/crm/business-partners")
public class BusinessPartnerDocumentController {
    private final DocumentAttachmentService service;

    public BusinessPartnerDocumentController(DocumentAttachmentService service) {
        this.service = service;
    }

    @GetMapping("/{businessPartnerId}/documents")
    public List<DocumentAttachmentResponse> list(@PathVariable Long businessPartnerId) {
        return service.listBusinessPartnerDocuments(businessPartnerId);
    }

    @PostMapping("/{businessPartnerId}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentAttachmentResponse upload(@PathVariable Long businessPartnerId,
                                             @RequestParam(value = "documentTypeCode", required = false) String documentTypeCode,
                                             @RequestParam("file") MultipartFile file) throws IOException {
        return service.uploadBusinessPartnerDocument(businessPartnerId, documentTypeCode, file);
    }

    @DeleteMapping("/documents/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable Long documentId) {
        service.archiveBusinessPartnerDocument(documentId);
    }
}

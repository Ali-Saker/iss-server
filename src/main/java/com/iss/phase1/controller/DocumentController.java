package com.iss.phase1.controller;

import com.iss.phase1.client.entity.Document;
import com.iss.phase1.client.entity.DocumentRequest;
import com.iss.phase1.client.extra.ActionType;
import com.iss.phase1.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    public Document fetch(DocumentRequest documentRequest) {

        String docName = documentRequest.getDocumentName();
        Document document = documentRepository.findTopByName(docName);
        if(document == null) {
            throw new RuntimeException("Document not found!");
        }

        return document;
    }

    public Document update(DocumentRequest documentRequest) {

        String docName = documentRequest.getDocumentName();
        Document document = documentRepository.findTopByName(docName);
        if (document == null) {
            throw new RuntimeException("Document not found!");
        }

        if (documentRequest.getActionType() == ActionType.EDIT) {
            document.setContent(documentRequest.getUpdatedContent());
        }
        return documentRepository.save(document);
    }
}

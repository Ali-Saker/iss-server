package com.iss.phase1.client.entity;

import com.iss.phase1.client.extra.ActionType;

import java.io.Serializable;

public class DocumentRequest implements Serializable {

    private static final long serialVersionUID = 6529685098267757690L;

    private String documentName;

    private ActionType actionType;

    private String updatedContent;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getUpdatedContent() {
        return updatedContent;
    }

    public void setUpdatedContent(String updatedContent) {
        this.updatedContent = updatedContent;
    }
}

package com.iss.phase1.client.entity;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class Document extends BaseEntity{

    private String name;

    @Lob
    private String content;

    public Document() {
    }

    public Document(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

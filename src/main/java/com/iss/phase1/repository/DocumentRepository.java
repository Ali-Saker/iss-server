package com.iss.phase1.repository;

import com.iss.phase1.client.entity.Document;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends BaseRepository<Document> {
    Document findTopByName(String name);
}

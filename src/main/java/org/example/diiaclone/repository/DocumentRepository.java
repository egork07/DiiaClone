package org.example.diiaclone.repository;

import org.example.diiaclone.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByDocumentTypeContainingIgnoreCase(String documentType);

    List<Document> findByUserId(Long userId);

    List<Document> findByUserIdAndDocumentTypeContainingIgnoreCase(
            Long userId, String documentType);
}

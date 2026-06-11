package org.example.diiaclone.entity;

import org.example.diiaclone.entity.Document;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id;
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = 1000L + (long)(Math.random() * 9000);
        }
    }

    private String fullName;

    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents;

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public List<Document> getDocuments() { return documents; }
    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }
}
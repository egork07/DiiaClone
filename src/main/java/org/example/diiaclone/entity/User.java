package org.example.diiaclone.entity;

import org.example.diiaclone.entity.Document;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String email;

    @Column(unique = true)
    private String username;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents;

    public Long getId()                    { return id; }
    public String getFullName()            { return fullName; }
    public String getEmail()               { return email; }
    public String getUsername()            { return username; }
    public List<Document> getDocuments()   { return documents; }
    public void setId(Long id)             { this.id = id; }
    public void setFullName(String v)      { this.fullName = v; }
    public void setEmail(String v)         { this.email = v; }
    public void setUsername(String v)      { this.username = v; }
    public void setDocuments(List<Document> v) { this.documents = v; }
}
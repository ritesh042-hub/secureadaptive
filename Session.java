package com.example.secureadaptive.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(length = 1000)
    private String sessionTokenHash;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;
    
    private boolean revoked = false;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSessionTokenHash() { return sessionTokenHash; }
    public void setSessionTokenHash(String sessionTokenHash) { this.sessionTokenHash = sessionTokenHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
}

package com.sprint.bank.entity;


import java.time.Instant;

import com.sprint.bank.user.entity.User;

import jakarta.persistence.*;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_hash", columnList = "token_hash", unique = true),
                @Index(name = "idx_refresh_user", columnList = "user_id")
        }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "family_id", nullable = false, length = 36)
    private String familyId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected RefreshToken() {}

    public RefreshToken(
            User user,
            String tokenHash,
            Instant expiresAt,
            String familyId) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.familyId = familyId;
    }

    public User getUser() { return user; }
    public String getTokenHash() { return tokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
    public String getFamilyId() { return familyId; }

    public void revoke() {
        this.revoked = true;
    }
}
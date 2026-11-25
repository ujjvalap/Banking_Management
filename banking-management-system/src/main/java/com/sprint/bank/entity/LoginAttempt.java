package com.sprint.bank.entity;


import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(
        name = "login_attempts",
        indexes = @Index(
                name = "idx_login_email",
                columnList = "email"
        )
)
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private boolean successful;

    @Column(nullable = false)
    private Instant attemptedAt = Instant.now();

    protected LoginAttempt() {}

    public LoginAttempt(String email, boolean successful) {
        this.email = email;
        this.successful = successful;
    }

    public String getEmail() { return email; }
    public boolean isSuccessful() { return successful; }
    public Instant getAttemptedAt() { return attemptedAt; }
}
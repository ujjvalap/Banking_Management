package com.sprint.bank.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.bank.entity.EmailVerificationToken;

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);
}
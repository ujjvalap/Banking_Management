package com.sprint.bank.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.bank.entity.PasswordResetToken;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
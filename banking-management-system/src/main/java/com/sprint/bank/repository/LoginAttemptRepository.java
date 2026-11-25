package com.sprint.bank.repository;



import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.bank.entity.LoginAttempt;

public interface LoginAttemptRepository
        extends JpaRepository<LoginAttempt, Long> {

    long countByEmailAndSuccessfulFalseAndAttemptedAtAfter(
            String email,
            Instant after
    );
}
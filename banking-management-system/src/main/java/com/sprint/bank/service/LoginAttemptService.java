package com.sprint.bank.service;


import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.bank.entity.LoginAttempt;
import com.sprint.bank.repository.LoginAttemptRepository;

@Service
public class LoginAttemptService {

    private final LoginAttemptRepository repository;
    private final int maxAttempts;
    private final long lockDurationMinutes;

    public LoginAttemptService(
            LoginAttemptRepository repository,
            @Value("${app.security.max-login-attempts:5}") int maxAttempts,
            @Value("${app.security.lock-duration-minutes:15}") long lockDurationMinutes) {
        this.repository = repository;
        this.maxAttempts = maxAttempts;
        this.lockDurationMinutes = lockDurationMinutes;
    }

    @Transactional
    public void record(String email, boolean successful) {
        repository.save(new LoginAttempt(email, successful));
    }

    public boolean isLocked(String email) {

        Instant cutoff = Instant.now().minusSeconds(lockDurationMinutes * 60);

        long failures = repository
                .countByEmailAndSuccessfulFalseAndAttemptedAtAfter(
                        email,
                        cutoff
                );

        return failures >= maxAttempts;
    }
}
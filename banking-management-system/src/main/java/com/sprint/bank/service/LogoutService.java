package com.sprint.bank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogoutService {

    private final RefreshTokenService refreshTokenService;

    public LogoutService(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenService.revokeAll(userId);
    }
}
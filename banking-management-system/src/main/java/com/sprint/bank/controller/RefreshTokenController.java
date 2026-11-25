package com.sprint.bank.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.bank.dto.RefreshTokenRequest;
import com.sprint.bank.dto.TokenResponse;
import com.sprint.bank.service.RefreshTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping({"/api/v1/refresh", "/api/auth/refresh"})
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return refreshTokenService.rotate(request.refreshToken());
    }
}

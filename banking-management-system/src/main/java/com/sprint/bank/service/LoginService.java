package com.sprint.bank.service;


import java.util.Locale;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.sprint.bank.exception.AccountLockedException;
import com.sprint.bank.exception.InvalidCredentialsException;
import com.sprint.bank.dto.AuthResponse;
import com.sprint.bank.dto.LoginRequest;
import com.sprint.bank.security.jwt.JwtService;
import com.sprint.bank.security.principal.CustomUserDetails;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public LoginService(
            AuthenticationManager authenticationManager,
            LoginAttemptService loginAttemptService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.authenticationManager = authenticationManager;
        this.loginAttemptService = loginAttemptService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse login(LoginRequest request) {

        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (loginAttemptService.isLocked(email)) {
            throw new AccountLockedException();
        }

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    request.password()
                            )
                    );

            loginAttemptService.record(email, true);

            CustomUserDetails user =
                    (CustomUserDetails) authentication.getPrincipal();

            String accessToken =
                    jwtService.generateAccessToken(user);

            String refreshToken =
                    refreshTokenService.create(user.getId());

            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtService.getAccessExpiration() / 1000
            );

        } catch (BadCredentialsException exception) {
            loginAttemptService.record(email, false);
            throw new InvalidCredentialsException();
        }
    }

    public AuthResponse issueTokens(CustomUserDetails user) {
        return new AuthResponse(
                jwtService.generateAccessToken(user),
                refreshTokenService.create(user.getId()),
                "Bearer",
                jwtService.getAccessExpiration() / 1000);
    }
}
package com.sprint.bank.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.bank.entity.RefreshToken;
import com.sprint.bank.exception.InvalidTokenException;
import com.sprint.bank.repository.RefreshTokenRepository;
import com.sprint.bank.dto.TokenResponse;
import com.sprint.bank.repository.UserRepository;
import com.sprint.bank.security.jwt.JwtService;
import com.sprint.bank.security.principal.CustomUserDetails;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final long refreshExpiration;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository repository,
            UserRepository userRepository,
            JwtService jwtService,
            @Value("${app.jwt.refresh-expiration}") long refreshExpiration) {

        this.repository = repository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshExpiration = refreshExpiration;
    }

    @Transactional
    public String create(Long userId) {

        var user = userRepository.findById(userId)
                .orElseThrow(InvalidTokenException::new);

        String rawToken = generateRandomToken();

        repository.save(new RefreshToken(
                user,
                hash(rawToken),
                Instant.now().plusMillis(refreshExpiration),
                UUID.randomUUID().toString()
        ));

        return rawToken;
    }

    @Transactional(noRollbackFor = InvalidTokenException.class)
    public TokenResponse rotate(String rawToken) {

        String tokenHash = hash(rawToken);

        RefreshToken stored = repository
                .findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::new);

        if (stored.isRevoked() ||
                !stored.getExpiresAt().isAfter(Instant.now())) {

            // A revoked token reuse must invalidate the entire family.
            repository.findAllByFamilyId(stored.getFamilyId())
                    .forEach(RefreshToken::revoke);

            throw new InvalidTokenException();
        }

        stored.revoke();

        CustomUserDetails user = loadPrincipal(stored.getUser().getId());

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = generateRandomToken();

        repository.save(new RefreshToken(
                stored.getUser(),
                hash(newRefreshToken),
                Instant.now().plusMillis(refreshExpiration),
                stored.getFamilyId()
        ));

        return new TokenResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtService.getAccessExpiration() / 1000
        );
    }

    @Transactional
    public void revokeAll(Long userId) {
        repository.findAllByUser_IdAndRevokedFalse(userId)
                .forEach(RefreshToken::revoke);
    }

    private CustomUserDetails loadPrincipal(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(InvalidTokenException::new);

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                java.util.List.of(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        )
                )
        );
    }

    private String generateRandomToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(token.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
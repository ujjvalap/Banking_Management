package com.sprint.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sprint.bank.entity.RefreshToken;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByFamilyId(String familyId);

    List<RefreshToken> findAllByUser_IdAndRevokedFalse(Long userId);
}
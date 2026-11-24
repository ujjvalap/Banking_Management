package com.sprint.bank.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sprint.bank.entity.AppUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	private final SecretKey signingKey;
	private final Duration expiration;

	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration}") Duration expiration) {
		this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
		this.expiration = expiration;
	}

	public String createToken(AppUser user) {
		Instant issuedAt = Instant.now();
		return Jwts.builder()
				.subject(user.getEmail())
				.issuedAt(Date.from(issuedAt))
				.expiration(Date.from(issuedAt.plus(expiration)))
				.signWith(signingKey)
				.compact();
	}

	public String extractSubject(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return claims.getSubject();
	}

	public long getExpirationSeconds() {
		return expiration.toSeconds();
	}
}

package com.sprint.bank.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) {
}

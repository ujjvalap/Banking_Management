package com.sprint.bank.dto;

import com.sprint.bank.entity.AppUser;
import com.sprint.bank.entity.CustomerProfile;

public record UserResponse(Long id, String email, String role, String fullName, String phone, String address) {
	public static UserResponse from(AppUser user) {
		CustomerProfile profile = user.getProfile();
		return new UserResponse(
				user.getId(),
				user.getEmail(),
				user.getRole().name(),
				profile.getFullName(),
				profile.getPhone(),
				profile.getAddress());
	}
}

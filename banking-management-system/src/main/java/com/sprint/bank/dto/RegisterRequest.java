package com.sprint.bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Size(max = 120) String fullName,
		@NotBlank @Email @Size(max = 254) String email,
		@NotBlank @Size(min = 12, max = 72)
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).+$",
				message = "must contain lowercase, uppercase, and numeric characters") String password,
		@NotBlank @Pattern(regexp = "^\\+?[0-9 ()-]{7,20}$") String phone,
		@NotBlank @Size(max = 300) String address,
		@NotBlank @Pattern(regexp = "(?i)^[A-Z]{5}[0-9]{4}[A-Z]$") String pan) {
}

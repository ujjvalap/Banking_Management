package com.sprint.bank.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sprint.bank.dto.RegisterRequest;
import com.sprint.bank.entity.AppUser;
import com.sprint.bank.entity.CustomerProfile;
import com.sprint.bank.entity.Role;
import com.sprint.bank.repository.UserRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Component
public class AdminAccountInitializer implements ApplicationRunner {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final Validator validator;
	private final String email;
	private final String password;
	private final String fullName;
	private final String phone;
	private final String address;
	private final String pan;

	public AdminAccountInitializer(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			Validator validator,
			@Value("${app.admin.email}") String email,
			@Value("${app.admin.password}") String password,
			@Value("${app.admin.full-name}") String fullName,
			@Value("${app.admin.phone}") String phone,
			@Value("${app.admin.address}") String address,
			@Value("${app.admin.pan}") String pan) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.validator = validator;
		this.email = email;
		this.password = password;
		this.fullName = fullName;
		this.phone = phone;
		this.address = address;
		this.pan = pan;
	}

	@Override
	public void run(ApplicationArguments arguments) {
		if (areAllBlank(email, password, fullName, phone, address, pan)) {
			return;
		}

		RegisterRequest adminDetails = new RegisterRequest(fullName, email, password, phone, address, pan);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(adminDetails);
		boolean passwordTooLong = password.getBytes(StandardCharsets.UTF_8).length > 72;
		if (!violations.isEmpty() || passwordTooLong) {
			String reasons = violations.stream()
					.map(ConstraintViolation::getMessage)
					.distinct()
					.collect(Collectors.joining(" "));
			if (passwordTooLong) {
				reasons = (reasons.isEmpty() ? "" : reasons + " ") + "Password must be no more than 72 UTF-8 bytes.";
			}
			throw new IllegalStateException("ADMIN_* settings are invalid. " + reasons);
		}

		String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
		var existingUser = userRepository.findByEmail(normalizedEmail);
		if (existingUser.isPresent()) {
			if (existingUser.get().getRole() != Role.ADMIN) {
				throw new IllegalStateException("ADMIN_EMAIL belongs to a non-admin account.");
			}
			return;
		}

		AppUser admin = new AppUser(normalizedEmail, passwordEncoder.encode(password), Role.ADMIN);
		admin.setProfile(new CustomerProfile(fullName.trim(), phone.trim(), address.trim(), pan.trim().toUpperCase(Locale.ROOT)));
		userRepository.saveAndFlush(admin);
	}

	private boolean areAllBlank(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return false;
			}
		}
		return true;
	}
}

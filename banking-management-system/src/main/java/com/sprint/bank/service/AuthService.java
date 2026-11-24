package com.sprint.bank.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.bank.dto.AuthResponse;
import com.sprint.bank.dto.LoginRequest;
import com.sprint.bank.dto.RegisterRequest;
import com.sprint.bank.dto.UserResponse;
import com.sprint.bank.entity.AppUser;
import com.sprint.bank.entity.CustomerProfile;
import com.sprint.bank.entity.Role;
import com.sprint.bank.exception.EmailAlreadyRegisteredException;
import com.sprint.bank.repository.UserRepository;
import com.sprint.bank.security.JwtService;

@Service
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final String dummyPasswordHash;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.dummyPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		return createAuthResponse(createCustomer(request));
	}

	@Transactional
	public void registerCustomer(RegisterRequest request) {
		createCustomer(request);
	}

	private AppUser createCustomer(RegisterRequest request) {
		String email = normalizeEmail(request.email());
		if (userRepository.existsByEmail(email)) {
			throw new EmailAlreadyRegisteredException();
		}

		if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
			throw new IllegalArgumentException("Password must be no more than 72 UTF-8 bytes.");
		}

		AppUser user = new AppUser(email, passwordEncoder.encode(request.password()), Role.CUSTOMER);
		user.setProfile(new CustomerProfile(
				request.fullName().trim(),
				request.phone().trim(),
				request.address().trim(),
				request.pan().trim().toUpperCase(Locale.ROOT)));
		user = userRepository.saveAndFlush(user);
		return user;
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		AppUser user = userRepository.findByEmail(normalizeEmail(request.email())).orElse(null);
		if (user == null) {
			passwordEncoder.matches(request.password(), dummyPasswordHash);
			throw new BadCredentialsException("Email or password is incorrect.");
		}
		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new BadCredentialsException("Email or password is incorrect.");
		}
		return createAuthResponse(user);
	}

	@Transactional(readOnly = true)
	public UserResponse getCurrentUser(String email) {
		AppUser user = userRepository.findByEmail(normalizeEmail(email))
				.orElseThrow(() -> new BadCredentialsException("The authenticated account no longer exists."));
		return UserResponse.from(user);
	}

	private AuthResponse createAuthResponse(AppUser user) {
		return new AuthResponse(
				jwtService.createToken(user),
				"Bearer",
				jwtService.getExpirationSeconds(),
				UserResponse.from(user));
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}

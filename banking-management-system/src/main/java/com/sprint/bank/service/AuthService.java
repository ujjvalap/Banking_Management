package com.sprint.bank.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.bank.dto.AuthResponse;
import com.sprint.bank.dto.LoginRequest;
import com.sprint.bank.dto.RegisterRequest;
import com.sprint.bank.dto.UserResponse;
import com.sprint.bank.repository.UserRepository;
import com.sprint.bank.security.principal.CustomUserDetails;
import com.sprint.bank.user.entity.User;

@Service
public class AuthService {

    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final UserRepository userRepository;

    public AuthService(
            RegistrationService registrationService,
            LoginService loginService,
            UserRepository userRepository) {

        this.registrationService = registrationService;
        this.loginService = loginService;
        this.userRepository = userRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = registrationService.register(request);
        return loginService.issueTokens(toPrincipal(user));
    }

    @Transactional
    public void registerCustomer(RegisterRequest request) {
        registrationService.register(request);
    }

    public AuthResponse login(LoginRequest request) {
        return loginService.login(request);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole());
    }

    private CustomUserDetails toPrincipal(User user) {
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
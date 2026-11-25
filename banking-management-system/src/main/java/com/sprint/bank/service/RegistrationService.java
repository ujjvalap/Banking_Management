package com.sprint.bank.service;


import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.bank.exception.EmailAlreadyRegisteredException;
import com.sprint.bank.repository.UserRepository;
import com.sprint.bank.user.entity.User;
import com.sprint.bank.user.enums.Role;
import com.sprint.bank.dto.RegisterRequest;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must not exceed 72 UTF-8 bytes.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        User user = new User(
                request.fullName().trim(),
                email,
                passwordEncoder.encode(request.password()),
                request.phone().trim(),
                request.address().trim(),
                request.pan().trim().toUpperCase(Locale.ROOT),
                Role.CUSTOMER);
        return userRepository.save(user);
    }
}
package com.sprint.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.bank.security.principal.CustomUserDetails;
import com.sprint.bank.service.LogoutService;

@RestController
@RequestMapping({"/api/v1/logout", "/api/auth/logout"})
public class LogoutController {
    private final LogoutService logoutService;

    public LogoutController(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal CustomUserDetails user) {
        logoutService.logout(user.getId());
    }
}

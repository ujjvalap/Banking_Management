package com.sprint.bank.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sprint.bank.dto.RegisterRequest;
import com.sprint.bank.exception.EmailAlreadyRegisteredException;
import com.sprint.bank.service.AuthService;

import jakarta.validation.Valid;

@Controller
public class HomeController {
    private final AuthService authService;

    public HomeController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String home(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String registered,
            @RequestParam(required = false) String logout,
            Model model) {
        if (userDetails != null) {
            model.addAttribute("currentUser", authService.getCurrentUser(userDetails.getUsername()));
        }
        if (error != null) {
            addNotice(model, "Email or password is incorrect.", false);
        } else if (registered != null) {
            addNotice(model, "Your account is ready. Sign in to continue.", true);
        } else if (logout != null) {
            addNotice(model, "You have been signed out.", true);
        }
        model.addAttribute("registerMode", false);
        return "index";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute RegisterRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerMode", true);
            addNotice(model, "Please review your details and meet the password requirements.", false);
            return "index";
        }

        try {
            authService.registerCustomer(request);
        } catch (EmailAlreadyRegisteredException exception) {
            model.addAttribute("registerMode", true);
            addNotice(model, exception.getMessage(), false);
            return "index";
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("registerMode", true);
            addNotice(model, "An account with those details already exists.", false);
            return "index";
        }

        redirectAttributes.addAttribute("registered", "true");
        return "redirect:/";
    }

    private void addNotice(Model model, String message, boolean success) {
        model.addAttribute("noticeMessage", message);
        model.addAttribute("noticeSuccess", success);
    }
}

package com.sprint.bank.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100)
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        @Size(max = 150)
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 12, max = 72)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$",
                message = "Password must contain uppercase, lowercase, number and special character"
        )
        String password,

        @NotBlank(message = "Phone number is required")
        @Size(max = 20)
        @Pattern(regexp = "^[+()0-9 .-]{7,20}$", message = "Enter a valid phone number")
        String phone,

        @NotBlank(message = "Address is required")
        @Size(max = 300)
        String address,

        @NotBlank(message = "PAN is required")
        @Pattern(regexp = "(?i)^[A-Z]{5}[0-9]{4}[A-Z]$", message = "Enter a valid PAN")
        String pan

) {}
package com.sprint.bank.dto;

import com.sprint.bank.user.enums.Role;

public record UserResponse(String fullName, String email, String phone, String address, Role role) {
}

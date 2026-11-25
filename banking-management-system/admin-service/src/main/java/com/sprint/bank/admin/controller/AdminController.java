
package com.sprint.bank.admin.controller;

import com.sprint.bank.admin.dto.AccountDto;
import com.sprint.bank.admin.dto.CustomerDto;
import com.sprint.bank.admin.dto.StatusUpdateRequest;
import com.sprint.bank.admin.dto.TransactionDto;
import com.sprint.bank.admin.dto.TransactionFilterRequest;
import com.sprint.bank.admin.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/api/admin/customers")
    @ResponseBody
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {

        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    @GetMapping("/api/admin/accounts")
    @ResponseBody
    public ResponseEntity<List<AccountDto>> getAllAccounts() {

        return ResponseEntity.ok(adminService.getAllAccounts());
    }

    @PutMapping("/api/admin/accounts/{id}/status")
    @ResponseBody
    public ResponseEntity<AccountDto> updateAccountStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {

        return ResponseEntity.ok(
                adminService.updateAccountStatus(id, request));
    }

    @GetMapping("/api/admin/transactions")
    @ResponseBody
    public ResponseEntity<List<TransactionDto>> getTransactions(
            @ModelAttribute TransactionFilterRequest filterRequest) {

        return ResponseEntity.ok(
                adminService.filterTransactions(filterRequest));
    }
}

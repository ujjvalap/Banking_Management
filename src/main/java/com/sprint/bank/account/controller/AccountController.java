
package com.sprint.bank.account.controller;

import com.sprint.bank.account.dto.AccountRequest;
import com.sprint.bank.account.dto.AccountResponse;
import com.sprint.bank.account.dto.BalanceUpdateRequest;
import com.sprint.bank.account.dto.StatusUpdateRequest;
import com.sprint.bank.account.service.AccountService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Open a new account
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request) {

        AccountResponse response = accountService.createAccount(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get account by ID
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                accountService.getAccountById(accountId)
        );
    }

    // Get all accounts of a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomerId(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                accountService.getAccountsByCustomerId(customerId)
        );
    }

    // Deposit money
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody BalanceUpdateRequest request) {

        return ResponseEntity.ok(
                accountService.deposit(accountId, request)
        );
    }

    // Withdraw money
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @PathVariable Long accountId,
            @Valid @RequestBody BalanceUpdateRequest request) {

        return ResponseEntity.ok(
                accountService.withdraw(accountId, request)
        );
    }

    // Update account status
    @PutMapping("/{accountId}/status")
    public ResponseEntity<AccountResponse> updateAccountStatus(
            @PathVariable Long accountId,
            @Valid @RequestBody StatusUpdateRequest request) {

        return ResponseEntity.ok(
                accountService.updateAccountStatus(accountId, request)
        );
    }
}

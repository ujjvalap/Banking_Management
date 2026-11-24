package com.sprint.bank.transaction.controller;

import com.sprint.bank.transaction.dto.*;
import com.sprint.bank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/accounts/{id}/deposit")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public TransactionResponse deposit(@PathVariable Long id,
                                       @Valid @RequestBody DepositRequest request) {
        return transactionService.deposit(id, request);
    }

    @PostMapping("/accounts/{id}/withdraw")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public TransactionResponse withdraw(@PathVariable Long id,
                                        @Valid @RequestBody WithdrawRequest request) {
        return transactionService.withdraw(id, request);
    }

    @PostMapping("/transfers")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transactionService.transfer(request);
    }

    @GetMapping("/accounts/{id}/transactions")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public List<TransactionResponse> statement(@PathVariable Long id) {
        return transactionService.getStatement(id);
    }
}

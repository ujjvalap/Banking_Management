
package com.sprint.bank.account.controller;

import com.sprint.bank.account.dto.TransactionResponse;
import com.sprint.bank.account.dto.TransferRequest;
import com.sprint.bank.account.service.TransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @PathVariable Long accountId) {

        List<TransactionResponse> transactions =
                transactionService.getTransactionHistory(accountId);

        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(
            @RequestBody TransferRequest request) {

        transactionService.transferMoney(request);

        return ResponseEntity.ok(
                "Money transferred successfully");
    }
}

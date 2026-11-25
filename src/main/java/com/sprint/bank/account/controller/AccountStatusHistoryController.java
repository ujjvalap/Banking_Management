package com.sprint.bank.account.controller;

import com.sprint.bank.account.dto.AccountStatusHistoryResponse;
import com.sprint.bank.account.service.AccountStatusHistoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountStatusHistoryController {

    private final AccountStatusHistoryService historyService;

    public AccountStatusHistoryController(
            AccountStatusHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/{accountId}/status-history")
    public ResponseEntity<List<AccountStatusHistoryResponse>> getStatusHistory(
            @PathVariable Long accountId) {

        List<AccountStatusHistoryResponse> history =
                historyService.getStatusHistory(accountId);

        return ResponseEntity.ok(history);
    }
}
package com.sprint.bank.account.service.impl;

import com.sprint.bank.account.dto.AccountStatusHistoryResponse;
import com.sprint.bank.account.entity.Account;
import com.sprint.bank.account.entity.AccountStatusHistory;
import com.sprint.bank.account.repository.AccountRepository;
import com.sprint.bank.account.repository.AccountStatusHistoryRepository;
import com.sprint.bank.account.service.AccountStatusHistoryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AccountStatusHistoryServiceImpl
        implements AccountStatusHistoryService {

    private final AccountRepository accountRepository;
    private final AccountStatusHistoryRepository historyRepository;

    public AccountStatusHistoryServiceImpl(
            AccountRepository accountRepository,
            AccountStatusHistoryRepository historyRepository) {

        this.accountRepository = accountRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountStatusHistoryResponse> getStatusHistory(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Account not found with ID: " + accountId));

        List<AccountStatusHistory> historyList =
                historyRepository.findByAccountIdOrderByChangedAtDesc(
                        account.getId());

        return historyList.stream()
                .map(history -> new AccountStatusHistoryResponse(
                        history.getId(),
                        history.getPreviousStatus(),
                        history.getNewStatus(),
                        history.getChangedAt(),
                        history.getRemarks(),
                        account.getId()
                ))
                .toList();
    }
}
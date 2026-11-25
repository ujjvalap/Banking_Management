package com.sprint.bank.account.service;

import com.sprint.bank.account.dto.AccountRequest;
import com.sprint.bank.account.dto.AccountResponse;
import com.sprint.bank.account.dto.BalanceUpdateRequest;
import com.sprint.bank.account.dto.StatusUpdateRequest;

import java.util.List;

public interface AccountService {

    AccountResponse createAccount(AccountRequest request);

    AccountResponse getAccountById(Long accountId);

    List<AccountResponse> getAccountsByCustomerId(Long customerId);

    AccountResponse deposit(Long accountId, BalanceUpdateRequest request);

    AccountResponse withdraw(Long accountId, BalanceUpdateRequest request);

    AccountResponse updateAccountStatus(Long accountId, StatusUpdateRequest request);
}
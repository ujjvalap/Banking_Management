package com.sprint.bank.account.service.impl;

import com.sprint.bank.account.dto.AccountRequest;
import com.sprint.bank.account.dto.AccountResponse;
import com.sprint.bank.account.dto.BalanceUpdateRequest;
import com.sprint.bank.account.dto.StatusUpdateRequest;

import com.sprint.bank.account.entity.Account;
import com.sprint.bank.account.entity.AccountStatus;
import com.sprint.bank.account.entity.AccountStatusHistory;
import com.sprint.bank.account.entity.Transaction;
import com.sprint.bank.account.entity.TransactionType;

import com.sprint.bank.account.repository.AccountRepository;
import com.sprint.bank.account.repository.TransactionRepository;
import com.sprint.bank.account.service.AccountService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountServiceImpl(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public AccountResponse createAccount(AccountRequest request) {

        Account account = new Account();

        account.setCustomerId(request.getCustomerId());
        account.setAccountType(request.getAccountType());
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);

        Account savedAccount = accountRepository.save(account);

        return convertToResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long accountId) {

        Account account = findAccountById(accountId);

        return convertToResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {

        return accountRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // DEPOSIT
    @Override
    public AccountResponse deposit(
            Long accountId,
            BalanceUpdateRequest request) {

        Account account = findAccountById(accountId);

        checkAccountIsActive(account);

        BigDecimal amount = request.getAmount();

        account.setBalance(account.getBalance().add(amount));

        Account updatedAccount = accountRepository.save(account);

        saveTransaction(
                account,
                TransactionType.DEPOSIT,
                amount,
                "Amount deposited"
        );

        return convertToResponse(updatedAccount);
    }

    // WITHDRAW
    @Override
    public AccountResponse withdraw(
            Long accountId,
            BalanceUpdateRequest request) {

        Account account = findAccountById(accountId);

        checkAccountIsActive(account);

        BigDecimal amount = request.getAmount();

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient account balance"
            );
        }

        account.setBalance(account.getBalance().subtract(amount));

        Account updatedAccount = accountRepository.save(account);

        saveTransaction(
                account,
                TransactionType.WITHDRAWAL,
                amount,
                "Amount withdrawn"
        );

        return convertToResponse(updatedAccount);
    }

    // UPDATE ACCOUNT STATUS
    @Override
    public AccountResponse updateAccountStatus(
            Long accountId,
            StatusUpdateRequest request) {

        Account account = findAccountById(accountId);

        AccountStatus oldStatus = account.getStatus();
        AccountStatus newStatus = request.getStatus();

        if (oldStatus == newStatus) {
            throw new IllegalArgumentException(
                    "Account already has this status"
            );
        }

        AccountStatusHistory history = new AccountStatusHistory();

        history.setPreviousStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setRemarks(request.getRemarks());
        history.setAccount(account);

        account.getStatusHistory().add(history);
        account.setStatus(newStatus);

        Account updatedAccount = accountRepository.save(account);

        return convertToResponse(updatedAccount);
    }

    // SAVE TRANSACTION RECORD
    private void saveTransaction(
            Account account,
            TransactionType transactionType,
            BigDecimal amount,
            String description) {

        Transaction transaction = new Transaction();

        transaction.setAccount(account);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        transactionRepository.save(transaction);
    }

    // FIND ACCOUNT
    private Account findAccountById(Long accountId) {

        return accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Account not found with ID: " + accountId
                        )
                );
    }

    // CHECK ACCOUNT STATUS
    private void checkAccountIsActive(Account account) {

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Account is not active. Transaction is not allowed."
            );
        }
    }

    // GENERATE ACCOUNT NUMBER
    private String generateAccountNumber() {

        String accountNumber;

        do {
            accountNumber = "SB" + System.currentTimeMillis();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    // CONVERT ENTITY TO DTO
    private AccountResponse convertToResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus(),
                account.getCustomerId(),
                account.getCreatedAt()
        );
    }
}

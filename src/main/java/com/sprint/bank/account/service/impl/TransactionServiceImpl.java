
package com.sprint.bank.account.service.impl;

import com.sprint.bank.account.dto.TransactionResponse;
import com.sprint.bank.account.dto.TransferRequest;
import com.sprint.bank.account.entity.Account;
import com.sprint.bank.account.entity.AccountStatus;
import com.sprint.bank.account.entity.Transaction;
import com.sprint.bank.account.entity.TransactionType;
import com.sprint.bank.account.repository.AccountRepository;
import com.sprint.bank.account.repository.TransactionRepository;
import com.sprint.bank.account.service.TransactionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionHistory(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Account not found with ID: " + accountId));

        List<Transaction> transactions =
                transactionRepository.findByAccountIdOrderByTransactionDateDesc(
                        account.getId());

        return transactions.stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getTransactionType(),
                        transaction.getAmount(),
                        transaction.getTransactionDate(),
                        transaction.getDescription(),
                        account.getId()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void transferMoney(TransferRequest request) {

        Long fromAccountId = request.getFromAccountId();
        Long toAccountId = request.getToAccountId();
        BigDecimal amount = request.getAmount();

        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException(
                    "Source and destination accounts cannot be the same");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Source account not found"));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Destination account not found"));

        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Source account is not active");
        }

        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Destination account is not active");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient account balance");
        }

        fromAccount.setBalance(
                fromAccount.getBalance().subtract(amount));

        toAccount.setBalance(
                toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction debitTransaction = new Transaction();
        debitTransaction.setAccount(fromAccount);
        debitTransaction.setTransactionType(TransactionType.TRANSFER);
        debitTransaction.setAmount(amount);
        debitTransaction.setDescription(
                "Amount transferred to account " +
                        toAccount.getAccountNumber());

        Transaction creditTransaction = new Transaction();
        creditTransaction.setAccount(toAccount);
        creditTransaction.setTransactionType(TransactionType.TRANSFER);
        creditTransaction.setAmount(amount);
        creditTransaction.setDescription(
                "Amount received from account " +
                        fromAccount.getAccountNumber());

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);
    }
}

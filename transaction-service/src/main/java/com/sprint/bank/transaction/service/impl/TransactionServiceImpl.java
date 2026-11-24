package com.sprint.bank.transaction.service.impl;

import com.sprint.bank.transaction.client.AccountClient;
import com.sprint.bank.transaction.dto.*;
import com.sprint.bank.transaction.entity.Transaction;
import com.sprint.bank.transaction.entity.TransactionType;
import com.sprint.bank.transaction.entity.Transfer;
import com.sprint.bank.transaction.exception.InsufficientBalanceException;
import com.sprint.bank.transaction.exception.TransactionFailedException;
import com.sprint.bank.transaction.repository.TransactionRepository;
import com.sprint.bank.transaction.repository.TransferRepository;
import com.sprint.bank.transaction.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final AccountClient accountClient;
    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;

    public TransactionServiceImpl(AccountClient accountClient,
                                  TransactionRepository transactionRepository,
                                  TransferRepository transferRepository) {
        this.accountClient = accountClient;
        this.transactionRepository = transactionRepository;
        this.transferRepository = transferRepository;
    }

    @Override
    @Transactional
    public TransactionResponse deposit(Long accountId, DepositRequest request) {
        try {
            accountClient.deposit(accountId, request.amount());
            return toResponse(saveTransaction(accountId, TransactionType.DEPOSIT,
                    request.amount(), "Deposit"));
        } catch (RestClientException ex) {
            throw new TransactionFailedException("Account deposit failed");
        }
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(Long accountId, WithdrawRequest request) {
        AccountDto account;
        try {
            account = accountClient.getAccount(accountId);
        } catch (RestClientException ex) {
            throw new TransactionFailedException("Account could not be found");
        }

        if (account == null || account.balance() == null) {
            throw new TransactionFailedException("Account could not be found");
        }
        if (account.balance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        try {
            accountClient.withdraw(accountId, request.amount());
            return toResponse(saveTransaction(accountId, TransactionType.WITHDRAW,
                    request.amount(), "Withdrawal"));
        } catch (RestClientException ex) {
            throw new TransactionFailedException("Account withdrawal failed");
        }
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        if (request.sourceAccountId().equals(request.destinationAccountId())) {
            throw new TransactionFailedException("Source and destination accounts must be different");
        }

        AccountDto source;
        try {
            source = accountClient.getAccount(request.sourceAccountId());
        } catch (RestClientException ex) {
            throw new TransactionFailedException("Source account could not be found");
        }
        if (source == null || source.balance() == null
                || source.balance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        try {
            accountClient.withdraw(request.sourceAccountId(), request.amount());
        } catch (RestClientException ex) {
            throw new TransactionFailedException("Transfer withdrawal failed");
        }

        try {
            accountClient.deposit(request.destinationAccountId(), request.amount());
        } catch (RestClientException ex) {
            accountClient.rollbackWithdrawal(request.sourceAccountId(), request.amount());
            throw new TransactionFailedException("Transfer deposit failed");
        }

        Transfer transfer = new Transfer();
        transfer.setSourceAccountId(request.sourceAccountId());
        transfer.setDestinationAccountId(request.destinationAccountId());
        transfer.setAmount(request.amount());
        transferRepository.save(transfer);

        saveTransaction(request.sourceAccountId(), TransactionType.TRANSFER,
                request.amount(), "Transfer to account " + request.destinationAccountId());
        return toResponse(saveTransaction(request.destinationAccountId(), TransactionType.TRANSFER,
                request.amount(), "Transfer from account " + request.sourceAccountId()));
    }

    @Override
    public List<TransactionResponse> getStatement(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTimestampDesc(accountId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Transaction saveTransaction(Long accountId, TransactionType type,
                                        java.math.BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        return transactionRepository.save(transaction);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getTimestamp(),
                transaction.getDescription());
    }
}

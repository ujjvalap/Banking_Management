package com.sprint.bank.transaction.service;

import com.sprint.bank.transaction.dto.DepositRequest;
import com.sprint.bank.transaction.dto.TransactionResponse;
import com.sprint.bank.transaction.dto.TransferRequest;
import com.sprint.bank.transaction.dto.WithdrawRequest;

import java.util.List;

public interface TransactionService {
    TransactionResponse deposit(Long accountId, DepositRequest request);
    TransactionResponse withdraw(Long accountId, WithdrawRequest request);
    TransactionResponse transfer(TransferRequest request);
    List<TransactionResponse> getStatement(Long accountId);
}

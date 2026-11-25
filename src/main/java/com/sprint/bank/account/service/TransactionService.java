package com.sprint.bank.account.service;

import com.sprint.bank.account.dto.TransactionResponse;
import com.sprint.bank.account.dto.TransferRequest;

import java.util.List;

public interface TransactionService {

    List<TransactionResponse> getTransactionHistory(Long accountId);

    void transferMoney(TransferRequest request);
}

package com.sprint.bank.admin.service.impl;

import com.sprint.bank.admin.client.AccountClient;
import com.sprint.bank.admin.client.AuthClient;
import com.sprint.bank.admin.client.TransactionClient;
import com.sprint.bank.admin.dto.AccountDto;
import com.sprint.bank.admin.dto.CustomerDto;
import com.sprint.bank.admin.dto.StatusUpdateRequest;
import com.sprint.bank.admin.dto.TransactionDto;
import com.sprint.bank.admin.dto.TransactionFilterRequest;
import com.sprint.bank.admin.entity.AdminAuditLog;
import com.sprint.bank.admin.exception.RemoteServiceException;
import com.sprint.bank.admin.repository.AdminAuditLogRepository;
import com.sprint.bank.admin.service.AdminService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AuthClient authClient;
    private final AccountClient accountClient;
    private final TransactionClient transactionClient;
    private final AdminAuditLogRepository auditLogRepository;

    public AdminServiceImpl(
            AuthClient authClient,
            AccountClient accountClient,
            TransactionClient transactionClient,
            AdminAuditLogRepository auditLogRepository) {

        this.authClient = authClient;
        this.accountClient = accountClient;
        this.transactionClient = transactionClient;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        try {
            return authClient.getAllCustomers();
        } catch (Exception exception) {
            throw new RemoteServiceException(
                    "Unable to fetch customers from Auth Service", exception);
        }
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        try {
            return accountClient.getAllAccounts();
        } catch (Exception exception) {
            throw new RemoteServiceException(
                    "Unable to fetch accounts from Account Service", exception);
        }
    }

    @Override
    public AccountDto updateAccountStatus(
            Long accountId, StatusUpdateRequest request) {

        try {
            AccountDto updatedAccount =
                    accountClient.updateAccountStatus(accountId, request);

            String action = request.getStatus();

            AdminAuditLog auditLog = new AdminAuditLog(
                    1L,
                    accountId,
                    action,
                    "Account status changed to " + action
            );

            auditLogRepository.save(auditLog);

            return updatedAccount;

        } catch (RemoteServiceException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new RemoteServiceException(
                    "Unable to update account status", exception);
        }
    }

    @Override
    public List<TransactionDto> getAllTransactions() {
        try {
            return transactionClient.getAllTransactions();
        } catch (Exception exception) {
            throw new RemoteServiceException(
                    "Unable to fetch transactions from Transaction Service",
                    exception);
        }
    }

    @Override
    public List<TransactionDto> filterTransactions(
            TransactionFilterRequest filterRequest) {

        List<TransactionDto> transactions = getAllTransactions();

        LocalDateTime fromDateTime = filterRequest.getFromDate() == null
                ? null : filterRequest.getFromDate().atStartOfDay();

        LocalDateTime toDateTime = filterRequest.getToDate() == null
                ? null : filterRequest.getToDate().atTime(23, 59, 59, 999999999);

        if (fromDateTime != null && toDateTime != null
                && fromDateTime.isAfter(toDateTime)) {
            throw new IllegalArgumentException(
                    "From date cannot be after to date");
        }

        return transactions.stream()

                .filter(transaction ->
                        fromDateTime == null
                                || (transaction.getTimestamp() != null
                                && !transaction.getTimestamp().isBefore(fromDateTime)))

                .filter(transaction ->
                        toDateTime == null
                                || (transaction.getTimestamp() != null
                                && !transaction.getTimestamp().isAfter(toDateTime)))

                .filter(transaction ->
                        filterRequest.getType() == null
                                || filterRequest.getType().isBlank()
                                || Objects.equals(
                                transaction.getType(),
                                filterRequest.getType()))

                .collect(Collectors.toList());
    }
}


package com.sprint.bank.admin.service;

import com.sprint.bank.admin.dto.AccountDto;
import com.sprint.bank.admin.dto.CustomerDto;
import com.sprint.bank.admin.dto.StatusUpdateRequest;
import com.sprint.bank.admin.dto.TransactionDto;
import com.sprint.bank.admin.dto.TransactionFilterRequest;

import java.util.List;

public interface AdminService {

    List<CustomerDto> getAllCustomers();

    List<AccountDto> getAllAccounts();

    AccountDto updateAccountStatus(Long accountId, StatusUpdateRequest request);

    List<TransactionDto> getAllTransactions();

    List<TransactionDto> filterTransactions(TransactionFilterRequest filterRequest);
}

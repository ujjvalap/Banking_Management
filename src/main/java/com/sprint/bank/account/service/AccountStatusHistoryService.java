package com.sprint.bank.account.service;

import com.sprint.bank.account.dto.AccountStatusHistoryResponse;
import java.util.List;

public interface AccountStatusHistoryService {

    List<AccountStatusHistoryResponse> getStatusHistory(Long accountId);
}
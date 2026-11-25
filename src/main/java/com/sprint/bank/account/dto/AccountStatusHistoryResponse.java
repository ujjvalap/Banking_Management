
package com.sprint.bank.account.dto;

import com.sprint.bank.account.entity.AccountStatus;
import java.time.LocalDateTime;

public class AccountStatusHistoryResponse {

    private Long id;
    private AccountStatus previousStatus;
    private AccountStatus newStatus;
    private LocalDateTime changedAt;
    private String remarks;
    private Long accountId;

    public AccountStatusHistoryResponse() {
    }

    public AccountStatusHistoryResponse(
            Long id,
            AccountStatus previousStatus,
            AccountStatus newStatus,
            LocalDateTime changedAt,
            String remarks,
            Long accountId) {

        this.id = id;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
        this.remarks = remarks;
        this.accountId = accountId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(AccountStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public AccountStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(AccountStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}

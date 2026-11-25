package com.sprint.bank.account.dto;

import com.sprint.bank.account.entity.AccountStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "Account status is required")
    private AccountStatus status;

    private String remarks;

    public StatusUpdateRequest() {
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
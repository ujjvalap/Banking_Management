
package com.sprint.bank.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class StatusUpdateRequest {

    @NotBlank(message = "Account status is required")
    @Pattern(
            regexp = "FREEZE|CLOSED",
            message = "Status must be FREEZE or CLOSED"
    )
    private String status;

    private String reason;

    // Default Constructor
    public StatusUpdateRequest() {
    }

    // Parameterized Constructor
    public StatusUpdateRequest(String status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    // Getters and Setters

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

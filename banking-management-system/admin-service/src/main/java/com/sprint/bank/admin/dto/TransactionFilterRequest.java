
package com.sprint.bank.admin.dto;

import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public class TransactionFilterRequest {

    private LocalDate fromDate;

    private LocalDate toDate;

    @Pattern(
            regexp = "DEPOSIT|WITHDRAW|TRANSFER",
            message = "Type must be DEPOSIT, WITHDRAW or TRANSFER"
    )
    private String type;

    public TransactionFilterRequest() {
    }

    public TransactionFilterRequest(LocalDate fromDate, LocalDate toDate, String type) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.type = type;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

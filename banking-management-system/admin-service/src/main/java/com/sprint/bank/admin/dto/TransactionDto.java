
package com.sprint.bank.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {

    private Long id;

    private String type;

    private BigDecimal amount;

    private LocalDateTime timestamp;

    private Long accountId;

    private String description;

    // Default Constructor
    public TransactionDto() {
    }

    // Parameterized Constructor
    public TransactionDto(Long id, String type,
                          BigDecimal amount,
                          LocalDateTime timestamp,
                          Long accountId,
                          String description) {

        this.id = id;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.accountId = accountId;
        this.description = description;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

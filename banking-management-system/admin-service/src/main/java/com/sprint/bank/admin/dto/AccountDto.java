
package com.sprint.bank.admin.dto;

import java.math.BigDecimal;

public class AccountDto {

    private Long id;

    private String accountNumber;

    private String type;

    private BigDecimal balance;

    private String status;

    private Long customerId;

    // Default Constructor
    public AccountDto() {
    }

    // Parameterized Constructor
    public AccountDto(Long id, String accountNumber,
                      String type, BigDecimal balance,
                      String status, Long customerId) {

        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.balance = balance;
        this.status = status;
        this.customerId = customerId;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}

package com.sprint.bank.transaction.dto;

import java.math.BigDecimal;

public record AccountDto(
        Long id,
        String accountNumber,
        String type,
        BigDecimal balance,
        String status
) {}

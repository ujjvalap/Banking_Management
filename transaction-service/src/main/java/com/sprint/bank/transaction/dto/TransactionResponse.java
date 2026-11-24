package com.sprint.bank.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long accountId,
        String type,
        BigDecimal amount,
        LocalDateTime timestamp,
        String description
) {}

package com.francis.banking.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
        String id,
        TransactionType type,
        BigDecimal amount,
        String description,
        Instant timestamp
) { }

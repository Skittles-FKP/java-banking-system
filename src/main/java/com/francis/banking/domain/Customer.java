package com.francis.banking.domain;

import java.time.Instant;

public record Customer(
        String id,
        String name,
        String email,
        Instant createdAt
) { }

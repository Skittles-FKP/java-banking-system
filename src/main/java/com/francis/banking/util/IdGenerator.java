package com.francis.banking.util;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {}

    public static String newId(String prefix) {
        // Short, readable IDs for demo purposes
        String raw = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        return prefix + "-" + raw;
    }
}

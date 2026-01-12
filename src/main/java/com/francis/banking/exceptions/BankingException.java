package com.francis.banking.exceptions;

public class BankingException extends RuntimeException {
    public BankingException(String message) {
        super(message);
    }
}

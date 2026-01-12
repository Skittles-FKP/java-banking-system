package com.francis.banking.exceptions;

public class NotFoundException extends BankingException {
    public NotFoundException(String message) {
        super(message);
    }
}

package com.francis.banking.domain;

import com.francis.banking.exceptions.InsufficientFundsException;
import com.francis.banking.exceptions.InvalidAmountException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {

    private final String accountNumber;
    private final String customerId;
    private final AccountType type;
    private final Instant openedAt;

    private BigDecimal balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, String customerId, AccountType type) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.type = type;
        this.openedAt = Instant.now();
        this.balance = BigDecimal.ZERO;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public AccountType getType() {
        return type;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void deposit(Transaction tx) {
        validateAmount(tx.amount());
        balance = balance.add(tx.amount());
        transactions.add(tx);
    }

    public void withdraw(Transaction tx) {
        validateAmount(tx.amount());
        if (balance.compareTo(tx.amount()) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Balance=" + balance + ", requested=" + tx.amount()
            );
        }
        balance = balance.subtract(tx.amount());
        transactions.add(tx);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be > 0");
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", type=" + type +
                ", balance=" + balance +
                ", openedAt=" + openedAt +
                '}';
    }
}

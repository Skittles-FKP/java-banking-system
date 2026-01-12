package com.francis.banking.services;

import com.francis.banking.domain.*;
import com.francis.banking.exceptions.NotFoundException;
import com.francis.banking.util.IdGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public class BankService {

    // In-memory stores (swap to DB later)
    private final Map<String, Customer> customers = new HashMap<>();
    private final Map<String, Account> accounts = new HashMap<>();

    public Customer createCustomer(String name, String email) {
        String id = IdGenerator.newId("CUS");
        Customer customer = new Customer(id, name, email, Instant.now());
        customers.put(id, customer);
        return customer;
    }

    public String openAccount(String customerId, AccountType type) {
        requireCustomer(customerId);

        String accountNumber = IdGenerator.newId("ACC");
        Account account = new Account(accountNumber, customerId, type);
        accounts.put(accountNumber, account);
        return accountNumber;
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = requireAccount(accountNumber);
        Transaction tx = new Transaction(
                IdGenerator.newId("TX"),
                TransactionType.DEPOSIT,
                amount,
                "Deposit into " + accountNumber,
                Instant.now()
        );
        account.deposit(tx);
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        Account account = requireAccount(accountNumber);
        Transaction tx = new Transaction(
                IdGenerator.newId("TX"),
                TransactionType.WITHDRAWAL,
                amount,
                "Withdraw from " + accountNumber,
                Instant.now()
        );
        account.withdraw(tx);
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        Account from = requireAccount(fromAccountNumber);
        Account to = requireAccount(toAccountNumber);

        Transaction out = new Transaction(
                IdGenerator.newId("TX"),
                TransactionType.TRANSFER_OUT,
                amount,
                "Transfer to " + toAccountNumber,
                Instant.now()
        );

        Transaction in = new Transaction(
                IdGenerator.newId("TX"),
                TransactionType.TRANSFER_IN,
                amount,
                "Transfer from " + fromAccountNumber,
                Instant.now()
        );

        // withdraw first (validates funds)
        from.withdraw(out);
        to.deposit(in);
    }

    public Account getAccount(String accountNumber) {
        return requireAccount(accountNumber);
    }

    public List<Transaction> getTransactions(String accountNumber) {
        return requireAccount(accountNumber).getTransactions();
    }

    public Customer getCustomer(String customerId) {
        return requireCustomer(customerId);
    }

    private Customer requireCustomer(String customerId) {
        Customer c = customers.get(customerId);
        if (c == null) throw new NotFoundException("Customer not found: " + customerId);
        return c;
    }

    private Account requireAccount(String accountNumber) {
        Account a = accounts.get(accountNumber);
        if (a == null) throw new NotFoundException("Account not found: " + accountNumber);
        return a;
    }
}

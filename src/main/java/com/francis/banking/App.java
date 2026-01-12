package com.francis.banking;

import com.francis.banking.domain.Account;
import com.francis.banking.domain.AccountType;
import com.francis.banking.domain.Customer;
import com.francis.banking.services.BankService;

import java.math.BigDecimal;

public class App {

    public static void main(String[] args) {

        BankService bankService = new BankService();

        System.out.println("=== Java Banking System Demo ===");

        // 1. Create customers (Customer is a record)
        Customer alice = bankService.createCustomer("Alice", "alice@example.com");
        Customer bob   = bankService.createCustomer("Bob", "bob@example.com");

        System.out.println("Created customers:");
        System.out.println(alice);
        System.out.println(bob);

        // 2. Open accounts (use record accessor id())
        String aliceAccount = bankService.openAccount(
                alice.id(),
                AccountType.CURRENT
        );

        String bobAccount = bankService.openAccount(
                bob.id(),
                AccountType.CURRENT
        );

        System.out.println("Opened accounts:");
        System.out.println("Alice Account: " + aliceAccount);
        System.out.println("Bob Account:   " + bobAccount);

        // 3. Deposit
        bankService.deposit(aliceAccount, new BigDecimal("500.00"));
        System.out.println("Deposited GBP 500.00 into Alice's account");

        // 4. Withdraw
        bankService.withdraw(aliceAccount, new BigDecimal("150.00"));
        System.out.println("Withdrew GBP 150.00 from Alice's account");

        // 5. Transfer
        bankService.transfer(
                aliceAccount,
                bobAccount,
                new BigDecimal("200.00")
        );
        System.out.println("Transferred GBP 200.00 from Alice to Bob");

        // 6. Final balances
        Account aliceFinal = bankService.getAccount(aliceAccount);
        Account bobFinal   = bankService.getAccount(bobAccount);

        System.out.println("Final balances:");
        System.out.println("Alice: GBP " + aliceFinal.getBalance());
        System.out.println("Bob:   GBP " + bobFinal.getBalance());

        // 7. Demonstrate error handling
        try {
            bankService.withdraw(aliceAccount, new BigDecimal("1000.00"));
        } catch (Exception ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }

        System.out.println("=== Demo complete ===");
    }
}

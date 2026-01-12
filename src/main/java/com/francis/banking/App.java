package com.francis.banking;

import com.francis.banking.domain.AccountType;
import com.francis.banking.domain.Customer;
import com.francis.banking.exceptions.BankingException;
import com.francis.banking.services.BankService;

import java.math.BigDecimal;
import java.util.Scanner;

public class App {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        BankService bank = new BankService();

        System.out.println("=== Java Banking System (Core Java) ===");

        // Seed demo data (optional)
        Customer demo = bank.createCustomer("Francis Prah", "francis@example.com");
        String acc1 = bank.openAccount(demo.id(), AccountType.CURRENT);
        String acc2 = bank.openAccount(demo.id(), AccountType.SAVINGS);
        bank.deposit(acc1, new BigDecimal("500.00"));
        bank.deposit(acc2, new BigDecimal("200.00"));

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> createCustomer(bank);
                    case "2" -> openAccount(bank);
                    case "3" -> deposit(bank);
                    case "4" -> withdraw(bank);
                    case "5" -> transfer(bank);
                    case "6" -> viewAccount(bank);
                    case "7" -> viewTransactions(bank);
                    case "0" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
            } catch (BankingException ex) {
                System.out.println("❌ Error: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("❌ Unexpected error: " + ex.getMessage());
            }

            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("""
                
                Choose an option:
                1) Create customer
                2) Open account
                3) Deposit
                4) Withdraw
                5) Transfer
                6) View account
                7) View transactions
                0) Exit
                """);
        System.out.print("> ");
    }

    private static void createCustomer(BankService bank) {
        System.out.print("Customer name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Customer email: ");
        String email = scanner.nextLine().trim();

        Customer c = bank.createCustomer(name, email);
        System.out.println("✅ Created customer: " + c);
    }

    private static void openAccount(BankService bank) {
        System.out.print("Customer ID: ");
        String customerId = scanner.nextLine().trim();

        System.out.print("Account type (CURRENT/SAVINGS): ");
        String t = scanner.nextLine().trim().toUpperCase();

        AccountType type = AccountType.valueOf(t);
        String accountNumber = bank.openAccount(customerId, type);

        System.out.println("✅ Opened " + type + " account: " + accountNumber);
    }

    private static void deposit(BankService bank) {
        System.out.print("Account number: ");
        String accountNumber = scanner.nextLine().trim();

        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());

        bank.deposit(accountNumber, amount);
        System.out.println("✅ Deposit successful.");
    }

    private static void withdraw(BankService bank) {
        System.out.print("Account number: ");
        String accountNumber = scanner.nextLine().trim();

        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());

        bank.withdraw(accountNumber, amount);
        System.out.println("✅ Withdrawal successful.");
    }

    private static void transfer(BankService bank) {
        System.out.print("From account: ");
        String from = scanner.nextLine().trim();

        System.out.print("To account: ");
        String to = scanner.nextLine().trim();

        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());

        bank.transfer(from, to, amount);
        System.out.println("✅ Transfer successful.");
    }

    private static void viewAccount(BankService bank) {
        System.out.print("Account number: ");
        String accountNumber = scanner.nextLine().trim();

        System.out.println(bank.getAccount(accountNumber));
    }

    private static void viewTransactions(BankService bank) {
        System.out.print("Account number: ");
        String accountNumber = scanner.nextLine().trim();

        bank.getTransactions(accountNumber).forEach(tx -> System.out.println(" - " + tx));
    }
}

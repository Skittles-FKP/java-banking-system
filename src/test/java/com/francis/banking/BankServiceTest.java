package com.francis.banking;

import com.francis.banking.domain.Account;
import com.francis.banking.domain.AccountType;
import com.francis.banking.domain.Customer;
import com.francis.banking.exceptions.InsufficientFundsException;
import com.francis.banking.exceptions.InvalidAmountException;
import com.francis.banking.exceptions.NotFoundException;
import com.francis.banking.services.BankService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {

    @Test
    void createCustomer_shouldCreateCustomerWithId() {
        BankService bank = new BankService();

        Customer c = bank.createCustomer("Francis Prah", "francis@example.com");

        assertNotNull(c);
        assertNotNull(c.id());
        assertTrue(c.id().startsWith("CUS-"));
        assertEquals("Francis Prah", c.name());
        assertEquals("francis@example.com", c.email());
        assertNotNull(c.createdAt());
    }

    @Test
    void openAccount_shouldCreateAccountLinkedToCustomer() {
        BankService bank = new BankService();
        Customer c = bank.createCustomer("Test User", "test@example.com");

        String acc = bank.openAccount(c.id(), AccountType.CURRENT);
        Account account = bank.getAccount(acc);

        assertNotNull(acc);
        assertTrue(acc.startsWith("ACC-"));
        assertEquals(c.id(), account.getCustomerId());
        assertEquals(AccountType.CURRENT, account.getType());
        assertEquals(new BigDecimal("0"), account.getBalance());
    }

    @Test
    void deposit_shouldIncreaseBalanceAndAddTransaction() {
        BankService bank = new BankService();
        Customer c = bank.createCustomer("Test User", "test@example.com");
        String acc = bank.openAccount(c.id(), AccountType.SAVINGS);

        bank.deposit(acc, new BigDecimal("100.00"));
        Account account = bank.getAccount(acc);

        assertEquals(new BigDecimal("100.00"), account.getBalance());
        assertEquals(1, bank.getTransactions(acc).size());
        assertEquals("Deposit into " + acc, bank.getTransactions(acc).get(0).description());
    }

    @Test
    void deposit_shouldRejectZeroOrNegativeAmounts() {
        BankService bank = new BankService();
        Customer c = bank.createCustomer("Test User", "test@example.com");
        String acc = bank.openAccount(c.id(), AccountType.SAVINGS);

        assertThrows(InvalidAmountException.class, () -> bank.deposit(acc, new BigDecimal("0")));
        assertThrows(InvalidAmountException.class, () -> bank.deposit(acc, new BigDecimal("-1")));
    }

    @Test
    void withdraw_shouldDecreaseBalanceAndAddTransaction() {
        BankService bank = new BankService();
        Customer c = bank.createCustomer("Test User", "test@example.com");
        String acc = bank.openAccount(c.id(), AccountType.CURRENT);

        bank.deposit(acc, new BigDecimal("200.00"));
        bank.withdraw(acc, new BigDecimal("50.00"));

        Account account = bank.getAccount(acc);
        assertEquals(new BigDecimal("150.00"), account.getBalance());
        assertEquals(2, bank.getTransactions(acc).size());
    }

    @Test
    void withdraw_shouldThrowIfInsufficientFunds() {
        BankService bank = new BankService();
        Customer c = bank.createCustomer("Test User", "test@example.com");
        String acc = bank.openAccount(c.id(), AccountType.CURRENT);

        bank.deposit(acc, new BigDecimal("20.00"));

        assertThrows(InsufficientFundsException.class, () -> bank.withdraw(acc, new BigDecimal("30.00")));
    }

    @Test
    void transfer_shouldMoveMoneyBetweenAccountsAndCreateTransactions() {
        BankService bank = new BankService();
        Customer c = bank.createCustomer("Test User", "test@example.com");

        String from = bank.openAccount(c.id(), AccountType.CURRENT);
        String to = bank.openAccount(c.id(), AccountType.SAVINGS);

        bank.deposit(from, new BigDecimal("300.00"));
        bank.transfer(from, to, new BigDecimal("125.50"));

        assertEquals(new BigDecimal("174.50"), bank.getAccount(from).getBalance());
        assertEquals(new BigDecimal("125.50"), bank.getAccount(to).getBalance());

        // From account: deposit + transfer out
        assertEquals(2, bank.getTransactions(from).size());
        // To account: transfer in
        assertEquals(1, bank.getTransactions(to).size());
    }

    @Test
    void transfer_shouldRejectSameAccount() {
        BankService bank = new BankService();
        Customer c = bank.createCustomer("Test User", "test@example.com");
        String acc = bank.openAccount(c.id(), AccountType.CURRENT);

        bank.deposit(acc, new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> bank.transfer(acc, acc, new BigDecimal("10.00")));
    }

    @Test
    void operations_shouldThrowNotFoundForUnknownIds() {
        BankService bank = new BankService();

        assertThrows(NotFoundException.class, () -> bank.getCustomer("CUS-DOESNOTEXIST"));
        assertThrows(NotFoundException.class, () -> bank.getAccount("ACC-DOESNOTEXIST"));
        assertThrows(NotFoundException.class, () -> bank.deposit("ACC-DOESNOTEXIST", new BigDecimal("10.00")));
    }
}

package banking.controller;

import banking.model.Account;
import banking.service.AccountService;

/**
 * Controller for account-related operations.
 * Delegates business logic to the AccountService layer.
 * 
 * @author Banking System
 */
public class AccountController {
    private AccountService accountService;

    /**
     * Constructs an AccountController with a default AccountService.
     */
    public AccountController() {
        this.accountService = new AccountService();
    }

    /**
     * Constructs an AccountController with a specified AccountService.
     * Useful for testing with mock services.
     * 
     * @param accountService The account service to use
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Deposits money into an account.
     * 
     * @param account The account to deposit into
     * @param amount The amount to deposit
     * @throws IllegalArgumentException if amount is not positive
     */
    public void deposit(Account account, double amount) {
        accountService.deposit(account, amount);
    }

    /**
     * Withdraws money from an account.
     * 
     * @param account The account to withdraw from
     * @param amount The amount to withdraw
     * @throws UnsupportedOperationException if withdrawals are not allowed for this account type
     * @throws IllegalArgumentException if amount is invalid or insufficient funds
     */
    public void withdraw(Account account, double amount) {
        accountService.withdraw(account, amount);
    }

    /**
     * Applies monthly interest to an account.
     * 
     * @param account The account to apply interest to
     */
    public void applyInterest(Account account) {
        accountService.applyMonthlyInterest(account);
    }

    /**
     * Gets the balance of an account.
     * 
     * @param account The account
     * @return The account balance
     */
    public double getBalance(Account account) {
        return account.getBalance();
    }
}

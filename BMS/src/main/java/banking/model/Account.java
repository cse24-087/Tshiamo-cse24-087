package banking.model;

/**
 * Abstract base class representing a bank account.
 * All account types (SavingsAccount, InvestmentAccount, ChequeAccount)
 * extend this class and implement account-specific behaviors.
 * 
 * @author Banking System
 */
public abstract class Account {
    protected int id;
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected int customerId;

    /**
     * Constructs an Account with the specified details.
     * 
     * @param id The unique account ID
     * @param accountNumber The account number
     * @param balance The initial balance
     * @param branch The branch name
     * @param customerId The ID of the customer who owns this account
     */
    public Account(int id, String accountNumber, double balance, String branch, int customerId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.customerId = customerId;
    }

    /**
     * Gets the account's unique ID.
     * 
     * @return The account ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the account number.
     * 
     * @return The account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Gets the current balance.
     * 
     * @return The balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Gets the branch name.
     * 
     * @return The branch name
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Gets the customer ID who owns this account.
     * 
     * @return The customer ID
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the account balance.
     * Used internally by deposit/withdraw/interest operations.
     * 
     * @param balance The new balance
     */
    protected void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Deposits money into the account.
     * 
     * @param amount The amount to deposit (must be positive)
     * @throws IllegalArgumentException if amount is not positive
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        balance += amount;
    }

    /**
     * Withdraws money from the account.
     * Implementation depends on account type.
     * 
     * @param amount The amount to withdraw
     * @throws UnsupportedOperationException if withdrawals are not allowed for this account type
     * @throws IllegalArgumentException if amount is invalid or insufficient funds
     */
    public abstract void withdraw(double amount);

    /**
     * Applies monthly interest to the account balance.
     * Interest rate and application logic depend on account type.
     */
    public abstract void applyMonthlyInterest();

    /**
     * Gets the account type as a string.
     * 
     * @return The account type (SAVINGS, INVESTMENT, or CHEQUE)
     */
    public abstract String getAccountType();
}

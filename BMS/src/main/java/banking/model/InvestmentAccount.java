package banking.model;

/**
 * Represents an Investment Account.
 * Investment accounts allow both deposits and withdrawals.
 * Minimum initial deposit: 500.00 BWP
 * Monthly interest rate: 5%
 * 
 * @author Banking System
 */
public class InvestmentAccount extends Account {
    private static final double INTEREST_RATE = 0.05; // 5% monthly
    private static final double MINIMUM_DEPOSIT = 500.0; // Minimum initial deposit in BWP

    /**
     * Constructs an InvestmentAccount.
     * Note: The minimum deposit validation should be done before creating the account
     * (e.g., in the service layer), not in the constructor.
     * 
     * @param id The unique account ID
     * @param accountNumber The account number
     * @param balance The initial balance (should be >= 500.00 BWP)
     * @param branch The branch name
     * @param customerId The ID of the customer who owns this account
     */
    public InvestmentAccount(int id, String accountNumber, double balance, String branch, int customerId) {
        super(id, accountNumber, balance, branch, customerId);
    }

    /**
     * Gets the minimum deposit required for an Investment account.
     * 
     * @return The minimum deposit amount (500.00 BWP)
     */
    public static double getMinimumDeposit() {
        return MINIMUM_DEPOSIT;
    }

    /**
     * Withdraws money from the Investment account.
     * 
     * @param amount The amount to withdraw (must be positive and not exceed balance)
     * @throws IllegalArgumentException if amount is not positive or exceeds balance
     */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        balance -= amount;
    }

    /**
     * Applies monthly interest of 5% to the account balance.
     */
    @Override
    public void applyMonthlyInterest() {
        balance += balance * INTEREST_RATE;
    }

    /**
     * Gets the account type.
     * 
     * @return "INVESTMENT"
     */
    @Override
    public String getAccountType() {
        return "INVESTMENT";
    }
}

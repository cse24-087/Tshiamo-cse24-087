package banking.model;

/**
 * Represents a Savings Account.
 * Savings accounts allow deposits but do not allow withdrawals.
 * Monthly interest rate: 0.05%
 * 
 * @author Banking System
 */
public class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.0005; // 0.05% monthly

    /**
     * Constructs a SavingsAccount.
     * 
     * @param id The unique account ID
     * @param accountNumber The account number
     * @param balance The initial balance
     * @param branch The branch name
     * @param customerId The ID of the customer who owns this account
     */
    public SavingsAccount(int id, String accountNumber, double balance, String branch, int customerId) {
        super(id, accountNumber, balance, branch, customerId);
    }

    /**
     * Withdrawals are not allowed from Savings accounts.
     * 
     * @param amount The amount to withdraw (ignored)
     * @throws UnsupportedOperationException always, as withdrawals are not allowed
     */
    @Override
    public void withdraw(double amount) {
        throw new UnsupportedOperationException("Withdrawals not allowed from a Savings Account.");
    }

    /**
     * Applies monthly interest of 0.05% to the account balance.
     */
    @Override
    public void applyMonthlyInterest() {
        balance += balance * INTEREST_RATE;
    }

    /**
     * Gets the account type.
     * 
     * @return "SAVINGS"
     */
    @Override
    public String getAccountType() {
        return "SAVINGS";
    }
}

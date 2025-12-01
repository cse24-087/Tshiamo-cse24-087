package banking.model;

/**
 * Represents a Cheque Account.
 * Cheque accounts allow both deposits and withdrawals.
 * Requirement: Customer must have employment information (employer name and address).
 * Cheque accounts do not earn interest.
 * 
 * @author Banking System
 */
public class ChequeAccount extends Account {
    private String employerName;
    private String employerAddress;

    /**
     * Constructs a ChequeAccount.
     * Note: Employment information validation should be done before creating the account
     * (e.g., in the service layer).
     * 
     * @param id The unique account ID
     * @param accountNumber The account number
     * @param balance The initial balance
     * @param branch The branch name
     * @param customerId The ID of the customer who owns this account
     * @param employerName The employer's name (required)
     * @param employerAddress The employer's address (required)
     */
    public ChequeAccount(int id, String accountNumber, double balance, String branch, 
                         int customerId, String employerName, String employerAddress) {
        super(id, accountNumber, balance, branch, customerId);
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    /**
     * Gets the employer's name.
     * 
     * @return The employer name
     */
    public String getEmployerName() {
        return employerName;
    }

    /**
     * Gets the employer's address.
     * 
     * @return The employer address
     */
    public String getEmployerAddress() {
        return employerAddress;
    }

    /**
     * Withdraws money from the Cheque account.
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
     * Cheque accounts do not earn interest.
     * This method does nothing.
     */
    @Override
    public void applyMonthlyInterest() {
        // Cheque accounts do not earn interest
    }

    /**
     * Gets the account type.
     * 
     * @return "CHEQUE"
     */
    @Override
    public String getAccountType() {
        return "CHEQUE";
    }
}

package banking.service;

import banking.dao.AccountDAO;
import banking.dao.CustomerDAO;
import banking.model.Account;
import banking.model.Customer;
import banking.model.InvestmentAccount;
import banking.model.SavingsAccount;
import banking.model.ChequeAccount;

/**
 * Service layer for account-related business operations.
 * This class enforces banking rules and validation before performing operations.
 * 
 * @author Banking System
 */
public class AccountService {
    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;

    /**
     * Constructs an AccountService with default DAO instances.
     */
    public AccountService() {
        this.accountDAO = new AccountDAO();
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Constructs an AccountService with specified DAO instances.
     * Useful for testing with mock DAOs.
     * 
     * @param accountDAO The account DAO
     * @param customerDAO The customer DAO
     */
    public AccountService(AccountDAO accountDAO, CustomerDAO customerDAO) {
        this.accountDAO = accountDAO;
        this.customerDAO = customerDAO;
    }

    /**
     * Deposits money into an account.
     * Validates the amount before depositing.
     * 
     * @param account The account to deposit into
     * @param amount The amount to deposit
     * @throws IllegalArgumentException if amount is not positive
     */
    public void deposit(Account account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        account.deposit(amount);
        accountDAO.updateAccount(account);
    }

    /**
     * Withdraws money from an account.
     * Validates the amount and account type before withdrawing.
     * 
     * @param account The account to withdraw from
     * @param amount The amount to withdraw
     * @throws UnsupportedOperationException if withdrawals are not allowed for this account type
     * @throws IllegalArgumentException if amount is invalid or insufficient funds
     */
    public void withdraw(Account account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        account.withdraw(amount);
        accountDAO.updateAccount(account);
    }

    /**
     * Applies monthly interest to an account.
     * 
     * @param account The account to apply interest to
     */
    public void applyMonthlyInterest(Account account) {
        account.applyMonthlyInterest();
        accountDAO.updateAccount(account);
    }

    /**
     * Creates a new SavingsAccount for a customer.
     * 
     * @param customerId The customer ID
     * @param accountNumber The account number
     * @param initialDeposit The initial deposit amount
     * @param branch The branch name
     * @return The created SavingsAccount
     * @throws IllegalArgumentException if initial deposit is not positive
     */
    public SavingsAccount createSavingsAccount(int customerId, String accountNumber, 
                                               double initialDeposit, String branch) {
        if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative.");
        }
        return accountDAO.createSavingsAccount(customerId, accountNumber, initialDeposit, branch);
    }

    /**
     * Creates a new InvestmentAccount for a customer.
     * Validates that the initial deposit meets the minimum requirement (500.00 BWP).
     * 
     * @param customerId The customer ID
     * @param accountNumber The account number
     * @param initialDeposit The initial deposit amount (must be >= 500.00 BWP)
     * @param branch The branch name
     * @return The created InvestmentAccount
     * @throws IllegalArgumentException if initial deposit is less than 500.00 BWP
     */
    public InvestmentAccount createInvestmentAccount(int customerId, String accountNumber, 
                                                     double initialDeposit, String branch) {
        if (initialDeposit < InvestmentAccount.getMinimumDeposit()) {
            throw new IllegalArgumentException(
                String.format("Investment account requires minimum deposit of BWP %.2f", 
                             InvestmentAccount.getMinimumDeposit()));
        }
        return accountDAO.createInvestmentAccount(customerId, accountNumber, initialDeposit, branch);
    }

    /**
     * Creates a new ChequeAccount for a customer using their existing employment information.
     * 
     * @param customerId The customer ID
     * @param accountNumber The account number
     * @param initialDeposit The initial deposit amount
     * @param branch The branch name
     * @return The created ChequeAccount
     * @throws IllegalArgumentException if customer does not have employment information
     *                                  or if initial deposit is negative
     */
    public ChequeAccount createChequeAccount(int customerId, String accountNumber, 
                                            double initialDeposit, String branch) {
        if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative.");
        }

        // Verify customer exists and has employment info
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        
        if (!customer.hasEmploymentInfo()) {
            throw new IllegalArgumentException(
                "Customer must have employment information (company name and address) to open a Cheque account.");
        }

        // Use customer's employment information
        return accountDAO.createChequeAccount(customerId, accountNumber, initialDeposit, branch,
                                             customer.getEmployerName(), customer.getEmployerAddress());
    }

    /**
     * Creates a new ChequeAccount for a customer.
     * If the customer doesn't have employment information, it can be provided here.
     * 
     * @param customerId The customer ID
     * @param accountNumber The account number
     * @param initialDeposit The initial deposit amount
     * @param branch The branch name
     * @param employerName The employer's name (required for cheque accounts)
     * @param employerAddress The employer's address (required for cheque accounts)
     * @return The created ChequeAccount
     * @throws IllegalArgumentException if employment information is missing or if initial deposit is negative
     */
    public ChequeAccount createChequeAccount(int customerId, String accountNumber, 
                                            double initialDeposit, String branch,
                                            String employerName, String employerAddress) {
        if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative.");
        }

        if (employerName == null || employerName.trim().isEmpty() ||
            employerAddress == null || employerAddress.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Employment information (company name and address) is required to open a Cheque account.");
        }

        // Verify customer exists
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found.");
        }
        
        // If customer doesn't have employment info, update it
        if (!customer.hasEmploymentInfo()) {
            try {
                customerDAO.updateEmploymentInfo(customerId, employerName, employerAddress);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update customer employment information", e);
            }
        }

        // Use provided employment information for the account
        return accountDAO.createChequeAccount(customerId, accountNumber, initialDeposit, branch,
                                             employerName, employerAddress);
    }

    /**
     * Gets an account by its account number.
     * 
     * @param accountNumber The account number
     * @return The account, or null if not found
     */
    public Account getAccountByNumber(String accountNumber) {
        return accountDAO.getAccountByNumber(accountNumber);
    }
}

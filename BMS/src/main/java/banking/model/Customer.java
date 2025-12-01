package banking.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the banking system.
 * A customer can have multiple accounts and must have employment information
 * to open a ChequeAccount.
 * 
 * @author Banking System
 */
public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String address;
    private String employerName;
    private String employerAddress;
    private List<Account> accounts = new ArrayList<>();

    /**
     * Constructs a Customer with basic information.
     * 
     * @param id The unique customer ID
     * @param firstName The customer's first name
     * @param lastName The customer's last name
     * @param address The customer's address
     */
    public Customer(int id, String firstName, String lastName, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    /**
     * Constructs a Customer with employment information.
     * 
     * @param id The unique customer ID
     * @param firstName The customer's first name
     * @param lastName The customer's last name
     * @param address The customer's address
     * @param employerName The employer's name
     * @param employerAddress The employer's address
     */
    public Customer(int id, String firstName, String lastName, String address, 
                    String employerName, String employerAddress) {
        this(id, firstName, lastName, address);
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    /**
     * Gets the customer's unique ID.
     * 
     * @return The customer ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the customer's first name.
     * 
     * @return The first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the customer's last name.
     * 
     * @return The last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the customer's address.
     * 
     * @return The address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the employer's name.
     * 
     * @return The employer name, or null if not set
     */
    public String getEmployerName() {
        return employerName;
    }

    /**
     * Gets the employer's address.
     * 
     * @return The employer address, or null if not set
     */
    public String getEmployerAddress() {
        return employerAddress;
    }

    /**
     * Checks if the customer has employment information.
     * Required for opening a ChequeAccount.
     * 
     * @return true if both employer name and address are present
     */
    public boolean hasEmploymentInfo() {
        return employerName != null && !employerName.trim().isEmpty() &&
               employerAddress != null && !employerAddress.trim().isEmpty();
    }

    /**
     * Adds an account to this customer's account list.
     * 
     * @param account The account to add
     */
    public void addAccount(Account account) {
        accounts.add(account);
    }

    /**
     * Gets all accounts associated with this customer.
     * 
     * @return A list of accounts
     */
    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    /**
     * Gets the customer's full name.
     * 
     * @return The full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

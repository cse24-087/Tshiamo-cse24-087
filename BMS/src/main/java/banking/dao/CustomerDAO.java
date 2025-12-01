package banking.dao;

import banking.model.Account;
import banking.model.Customer;
import banking.model.SavingsAccount;
import banking.model.InvestmentAccount;
import banking.model.ChequeAccount;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Customer entities.
 * Handles database operations for customers, including authentication
 * and loading customer accounts.
 * 
 * @author Banking System
 */
public class CustomerDAO {

    /**
     * Authenticates a user and returns the associated customer with their accounts.
     * 
     * @param username The username
     * @param password The password
     * @return The Customer object with loaded accounts, or null if authentication fails
     */
    public Customer getCustomerByCredentials(String username, String password) {
        String sql = "SELECT c.id, c.firstName, c.lastName, c.address, " +
                     "c.employerName, c.employerAddress " +
                     "FROM users u JOIN customers c ON u.customer_id = c.id " +
                     "WHERE u.username = ? AND u.password = ? AND u.customer_id IS NOT NULL LIMIT 1;";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int customerId = rs.getInt("id");
                    Customer customer = createCustomerFromResultSet(rs);
                    
                    // Load accounts for this customer
                    List<Account> accounts = loadAccountsForCustomer(customerId);
                    for (Account account : accounts) {
                        customer.addAccount(account);
                    }
                    return customer;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Gets a customer by their ID.
     * 
     * @param customerId The customer ID
     * @return The Customer object, or null if not found
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT id, firstName, lastName, address, employerName, employerAddress " +
                     "FROM customers WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = createCustomerFromResultSet(rs);
                    // Load accounts for this customer
                    List<Account> accounts = loadAccountsForCustomer(customerId);
                    for (Account account : accounts) {
                        customer.addAccount(account);
                    }
                    return customer;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a new customer and their user account.
     * 
     * @param firstName The customer's first name
     * @param lastName The customer's last name
     * @param address The customer's address
     * @param employerName The employer's name (can be null)
     * @param employerAddress The employer's address (can be null)
     * @param username The username for the user account
     * @param password The password for the user account
     * @return The created Customer object
     * @throws SQLException if a database error occurs
     */
    public Customer createCustomer(String firstName, String lastName, String address,
                                   String employerName, String employerAddress,
                                   String username, String password) throws SQLException {
        Connection conn = DBUtil.getConnection();
        try {
            conn.setAutoCommit(false);
            
            // Insert customer
            String customerSql = "INSERT INTO customers(firstName, lastName, address, employerName, employerAddress) " +
                                "VALUES (?, ?, ?, ?, ?)";
            int customerId;
            try (PreparedStatement ps = conn.prepareStatement(customerSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, address);
                ps.setString(4, employerName);
                ps.setString(5, employerAddress);
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        customerId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to get customer ID");
                    }
                }
            }
            
            // Insert user account
            String userSql = "INSERT INTO users(username, password, customer_id, employee_id) " +
                            "VALUES (?, ?, ?, NULL)";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setInt(3, customerId);
                ps.executeUpdate();
            }
            
            conn.commit();
            
            // Return the created customer
            if (employerName != null && !employerName.trim().isEmpty() &&
                employerAddress != null && !employerAddress.trim().isEmpty()) {
                return new Customer(customerId, firstName, lastName, address, employerName, employerAddress);
            } else {
                return new Customer(customerId, firstName, lastName, address);
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Updates a customer's employment information.
     * 
     * @param customerId The customer ID
     * @param employerName The employer's name
     * @param employerAddress The employer's address
     * @throws SQLException if a database error occurs
     */
    public void updateEmploymentInfo(int customerId, String employerName, String employerAddress) throws SQLException {
        String sql = "UPDATE customers SET employerName = ?, employerAddress = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employerName);
            ps.setString(2, employerAddress);
            ps.setInt(3, customerId);
            ps.executeUpdate();
        }
    }

    /**
     * Gets all customers from the database.
     * 
     * @return A list of all customers with their accounts loaded
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, firstName, lastName, address, employerName, employerAddress " +
                     "FROM customers ORDER BY id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Customer customer = createCustomerFromResultSet(rs);
                // Load accounts for this customer
                List<Account> accounts = loadAccountsForCustomer(customer.getId());
                for (Account account : accounts) {
                    customer.addAccount(account);
                }
                customers.add(customer);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return customers;
    }

    /**
     * Creates a Customer object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at the customer row
     * @return The Customer object
     * @throws SQLException if a database error occurs
     */
    private Customer createCustomerFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String address = rs.getString("address");
        String employerName = rs.getString("employerName");
        String employerAddress = rs.getString("employerAddress");

        // If employment info exists, use the constructor with employment info
        if (employerName != null && employerAddress != null) {
            return new Customer(id, firstName, lastName, address, employerName, employerAddress);
        } else {
            return new Customer(id, firstName, lastName, address);
        }
    }

    /**
     * Loads all accounts for a given customer.
     * 
     * @param customerId The customer ID
     * @return A list of accounts for the customer
     * @throws SQLException if a database error occurs
     */
    private List<Account> loadAccountsForCustomer(int customerId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id, accountNumber, balance, branch, type, " +
                     "employerName, employerAddress, customer_id " +
                     "FROM accounts WHERE customer_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Account account = mapToAccount(rs);
                    if (account != null) {
                        accounts.add(account);
                    }
                }
            }
        }
        return accounts;
    }

    /**
     * Maps a database ResultSet row to the appropriate Account subclass.
     * 
     * @param rs The ResultSet positioned at the account row
     * @return The Account object (SavingsAccount, InvestmentAccount, or ChequeAccount)
     * @throws SQLException if a database error occurs
     */
    private Account mapToAccount(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String accountNumber = rs.getString("accountNumber");
        double balance = rs.getDouble("balance");
        String branch = rs.getString("branch");
        String type = rs.getString("type");
        int customerId = rs.getInt("customer_id");
        String employerName = rs.getString("employerName");
        String employerAddress = rs.getString("employerAddress");

        return switch (type) {
            case "SAVINGS" -> new SavingsAccount(id, accountNumber, balance, branch, customerId);
            case "INVESTMENT" -> new InvestmentAccount(id, accountNumber, balance, branch, customerId);
            case "CHEQUE" -> new ChequeAccount(id, accountNumber, balance, branch, customerId,
                                               employerName, employerAddress);
            default -> null;
        };
    }
}

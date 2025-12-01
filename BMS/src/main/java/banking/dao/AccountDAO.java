package banking.dao;

import banking.model.*;
import java.sql.*;

/**
 * Data Access Object for Account entities.
 * Handles database operations for accounts, including CRUD operations
 * and mapping between database records and Account subclasses.
 * 
 * @author Banking System
 */
public class AccountDAO {

    /**
     * Updates an account's balance in the database.
     * 
     * @param account The account to update
     */
    public void updateAccount(Account account) {
        String sql = "UPDATE accounts SET balance = ? WHERE accountNumber = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, account.getBalance());
            ps.setString(2, account.getAccountNumber());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to update account balance", ex);
        }
    }

    /**
     * Creates a new SavingsAccount in the database.
     * 
     * @param customerId The customer ID
     * @param accountNumber The account number
     * @param balance The initial balance
     * @param branch The branch name
     * @return The created SavingsAccount
     */
    public SavingsAccount createSavingsAccount(int customerId, String accountNumber, 
                                               double balance, String branch) {
        String sql = "INSERT INTO accounts(accountNumber, balance, branch, type, customer_id) " +
                     "VALUES (?, ?, ?, 'SAVINGS', ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, accountNumber);
            ps.setDouble(2, balance);
            ps.setString(3, branch);
            ps.setInt(4, customerId);
            ps.executeUpdate();
            
            int accountId = getGeneratedId(ps);
            return new SavingsAccount(accountId, accountNumber, balance, branch, customerId);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to create savings account", ex);
        }
    }

    /**
     * Creates a new InvestmentAccount in the database.
     * 
     * @param customerId The customer ID
     * @param accountNumber The account number
     * @param balance The initial balance
     * @param branch The branch name
     * @return The created InvestmentAccount
     */
    public InvestmentAccount createInvestmentAccount(int customerId, String accountNumber, 
                                                     double balance, String branch) {
        String sql = "INSERT INTO accounts(accountNumber, balance, branch, type, customer_id) " +
                     "VALUES (?, ?, ?, 'INVESTMENT', ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, accountNumber);
            ps.setDouble(2, balance);
            ps.setString(3, branch);
            ps.setInt(4, customerId);
            ps.executeUpdate();
            
            int accountId = getGeneratedId(ps);
            return new InvestmentAccount(accountId, accountNumber, balance, branch, customerId);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to create investment account", ex);
        }
    }

    /**
     * Creates a new ChequeAccount in the database.
     * 
     * @param customerId The customer ID
     * @param accountNumber The account number
     * @param balance The initial balance
     * @param branch The branch name
     * @param employerName The employer's name
     * @param employerAddress The employer's address
     * @return The created ChequeAccount
     */
    public ChequeAccount createChequeAccount(int customerId, String accountNumber, 
                                             double balance, String branch,
                                             String employerName, String employerAddress) {
        String sql = "INSERT INTO accounts(accountNumber, balance, branch, type, " +
                     "employerName, employerAddress, customer_id) VALUES (?, ?, ?, 'CHEQUE', ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, accountNumber);
            ps.setDouble(2, balance);
            ps.setString(3, branch);
            ps.setString(4, employerName);
            ps.setString(5, employerAddress);
            ps.setInt(6, customerId);
            ps.executeUpdate();
            
            int accountId = getGeneratedId(ps);
            return new ChequeAccount(accountId, accountNumber, balance, branch, customerId,
                                    employerName, employerAddress);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to create cheque account", ex);
        }
    }

    /**
     * Gets an account by its account number.
     * 
     * @param accountNumber The account number
     * @return The account, or null if not found
     */
    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT id, accountNumber, balance, branch, type, employerName, " +
                     "employerAddress, customer_id FROM accounts WHERE accountNumber = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToAccount(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Maps a database ResultSet row to the appropriate Account subclass.
     * 
     * @param rs The ResultSet positioned at the row to map
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

    /**
     * Gets the generated ID from a PreparedStatement.
     * 
     * @param ps The PreparedStatement that executed an INSERT
     * @return The generated ID
     * @throws SQLException if a database error occurs
     */
    private int getGeneratedId(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("No ID obtained from insert");
            }
        }
    }
}

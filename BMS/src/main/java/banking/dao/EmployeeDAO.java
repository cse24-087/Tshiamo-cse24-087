package banking.dao;

import banking.model.Employee;
import java.sql.*;

/**
 * Data Access Object for Employee entities.
 * Handles database operations for employees, including authentication.
 * 
 * @author Banking System
 */
public class EmployeeDAO {

    /**
     * Authenticates an employee and returns the associated employee.
     * 
     * @param username The username
     * @param password The password
     * @return The Employee object, or null if authentication fails
     */
    public Employee getEmployeeByCredentials(String username, String password) {
        String sql = "SELECT e.id, e.firstName, e.lastName, e.email, e.role " +
                     "FROM users u JOIN employees e ON u.employee_id = e.id " +
                     "WHERE u.username = ? AND u.password = ? AND u.employee_id IS NOT NULL LIMIT 1;";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createEmployeeFromResultSet(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Creates an Employee object from a ResultSet.
     * 
     * @param rs The ResultSet positioned at the employee row
     * @return The Employee object
     * @throws SQLException if a database error occurs
     */
    private Employee createEmployeeFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String email = rs.getString("email");
        String role = rs.getString("role");
        return new Employee(id, firstName, lastName, email, role);
    }

    /**
     * Gets an employee by their ID.
     * 
     * @param employeeId The employee ID
     * @return The Employee object, or null if not found
     */
    public Employee getEmployeeById(int employeeId) {
        String sql = "SELECT id, firstName, lastName, email, role " +
                     "FROM employees WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createEmployeeFromResultSet(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}


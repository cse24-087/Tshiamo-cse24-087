package banking.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for database operations.
 * Handles database initialization, schema creation, and migrations.
 * 
 * @author Banking System
 */
public class DBUtil {
    private static final String DB_FILE = "banking.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;

    /**
     * Gets a database connection.
     * 
     * @return A Connection to the SQLite database
     * @throws SQLException if a database error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Initializes the database schema and migrates existing databases if needed.
     * Creates tables if they don't exist and adds missing columns for existing databases.
     * Inserts sample data if the database is empty.
     */
    public static void initDatabase() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            // Enable foreign keys
            st.execute("PRAGMA foreign_keys = ON;");

            // Create tables
            st.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "firstName TEXT NOT NULL, " +
                    "lastName TEXT NOT NULL, " +
                    "address TEXT, " +
                    "employerName TEXT, " +
                    "employerAddress TEXT" +
                    ");");

            st.execute("CREATE TABLE IF NOT EXISTS employees (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "firstName TEXT NOT NULL, " +
                    "lastName TEXT NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "role TEXT NOT NULL" +
                    ");");

            st.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL, " +
                    "customer_id INTEGER, " +
                    "employee_id INTEGER, " +
                    "FOREIGN KEY(customer_id) REFERENCES customers(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(employee_id) REFERENCES employees(id) ON DELETE CASCADE" +
                    ");");

            st.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "accountNumber TEXT NOT NULL, " +
                    "balance REAL NOT NULL, " +
                    "branch TEXT, " +
                    "type TEXT NOT NULL, " +
                    "employerName TEXT, " +
                    "employerAddress TEXT, " +
                    "customer_id INTEGER, " +
                    "FOREIGN KEY(customer_id) REFERENCES customers(id) ON DELETE CASCADE" +
                    ");");

            // Migrate existing databases: add employer columns if they don't exist
            migrateDatabase(conn);

            // Check whether data exists
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS cnt FROM customers;");
            int cnt = rs.next() ? rs.getInt("cnt") : 0;
            ResultSet rsEmp = st.executeQuery("SELECT COUNT(*) AS cnt FROM employees;");
            int cntEmp = rsEmp.next() ? rsEmp.getInt("cnt") : 0;
            if (cnt == 0 && cntEmp == 0) {
                insertSampleData(conn);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Migrates existing database schema by adding missing columns and tables.
     * Handles the case where the database exists but is missing new fields.
     * 
     * @param conn The database connection
     */
    private static void migrateDatabase(Connection conn) {
        try (Statement st = conn.createStatement()) {
            // Check if employerName column exists in customers table
            try {
                st.executeQuery("SELECT employerName FROM customers LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                try {
                    st.execute("ALTER TABLE customers ADD COLUMN employerName TEXT;");
                    st.execute("ALTER TABLE customers ADD COLUMN employerAddress TEXT;");
                } catch (SQLException ex) {
                    // Columns might already exist, ignore
                }
            }
            
            // Check if employee_id column exists in users table
            try {
                st.executeQuery("SELECT employee_id FROM users LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                try {
                    st.execute("ALTER TABLE users ADD COLUMN employee_id INTEGER;");
                } catch (SQLException ex) {
                    // Column might already exist, ignore
                }
            }
        } catch (SQLException ex) {
            // Migration failed, but continue
            ex.printStackTrace();
        }
    }

    /**
     * Inserts sample data into the database for testing purposes.
     * Creates 4 customers with 10 accounts total, 2 employees, and users for both.
     * 
     * @param conn The database connection
     * @throws SQLException if a database error occurs
     */
    private static void insertSampleData(Connection conn) throws SQLException {
        // Insert 4 customers and 10 accounts across them. Insert users for customers and employees.
        // Customers with Cheque accounts must have employment info
        String insertCustomer = "INSERT INTO customers(firstName, lastName, address, employerName, employerAddress) VALUES (?, ?, ?, ?, ?);";
        String insertEmployee = "INSERT INTO employees(firstName, lastName, email, role) VALUES (?, ?, ?, ?);";
        String insertUser = "INSERT INTO users(username, password, customer_id, employee_id) VALUES (?, ?, ?, ?);";
        String insertAccount = "INSERT INTO accounts(accountNumber, balance, branch, type, employerName, employerAddress, customer_id) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement pc = conn.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pe = conn.prepareStatement(insertEmployee, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pu = conn.prepareStatement(insertUser);
             PreparedStatement pa = conn.prepareStatement(insertAccount)) {

            // Customer 1 - Has Cheque account, so needs employment info
            pc.setString(1, "Katlego"); pc.setString(2, "Sekgoma"); pc.setString(3, "Gaborone");
            pc.setString(4, "Acme Corp"); pc.setString(5, "Gaborone");
            pc.executeUpdate();
            int cust1 = getGeneratedId(pc);
            pu.setString(1, "customer1"); pu.setString(2, "1234"); pu.setInt(3, cust1); pu.setObject(4, null); pu.executeUpdate();

            // Add 3 accounts
            pa.setString(1, "CHK-001"); pa.setDouble(2, 1200.0); pa.setString(3, "Main"); pa.setString(4, "CHEQUE");
            pa.setString(5, "Acme Corp"); pa.setString(6, "Gaborone"); pa.setInt(7, cust1); pa.executeUpdate();
            pa.setString(1, "INV-001"); pa.setDouble(2, 1500.0); pa.setString(3, "Main"); pa.setString(4, "INVESTMENT");
            pa.setString(5, null); pa.setString(6, null); pa.setInt(7, cust1); pa.executeUpdate();
            pa.setString(1, "SAV-001"); pa.setDouble(2, 300.0); pa.setString(3, "Main"); pa.setString(4, "SAVINGS");
            pa.setString(5, null); pa.setString(6, null); pa.setInt(7, cust1); pa.executeUpdate();

            // Customer 2 - No Cheque account, no employment info needed
            pc.setString(1, "Alice"); pc.setString(2, "Moloi"); pc.setString(3, "Francistown");
            pc.setString(4, null); pc.setString(5, null);
            pc.executeUpdate();
            int cust2 = getGeneratedId(pc);
            pu.setString(1, "customer2"); pu.setString(2, "1234"); pu.setInt(3, cust2); pu.setObject(4, null); pu.executeUpdate();
            pa.setString(1, "INV-002"); pa.setDouble(2, 800.0); pa.setString(3, "North"); pa.setString(4, "INVESTMENT");
            pa.setString(5, null); pa.setString(6, null); pa.setInt(7, cust2); pa.executeUpdate();
            pa.setString(1, "SAV-002"); pa.setDouble(2, 250.0); pa.setString(3, "North"); pa.setString(4, "SAVINGS");
            pa.setString(5, null); pa.setString(6, null); pa.setInt(7, cust2); pa.executeUpdate();

            // Customer 3 - Has Cheque account, so needs employment info
            pc.setString(1, "Brian"); pc.setString(2, "Kgosietsile"); pc.setString(3, "Maun");
            pc.setString(4, "Botswana Ltd"); pc.setString(5, "Maun");
            pc.executeUpdate();
            int cust3 = getGeneratedId(pc);
            pu.setString(1, "customer3"); pu.setString(2, "1234"); pu.setInt(3, cust3); pu.setObject(4, null); pu.executeUpdate();
            pa.setString(1, "CHK-002"); pa.setDouble(2, 500.0); pa.setString(3, "West"); pa.setString(4, "CHEQUE");
            pa.setString(5, "Botswana Ltd"); pa.setString(6, "Maun"); pa.setInt(7, cust3); pa.executeUpdate();
            pa.setString(1, "INV-003"); pa.setDouble(2, 700.0); pa.setString(3, "West"); pa.setString(4, "INVESTMENT");
            pa.setString(5, null); pa.setString(6, null); pa.setInt(7, cust3); pa.executeUpdate();

            // Customer 4 - Has Cheque account, so needs employment info
            pc.setString(1, "Dineo"); pc.setString(2, "Modise"); pc.setString(3, "Gaborone");
            pc.setString(4, "SmallBiz Pty"); pc.setString(5, "Gaborone");
            pc.executeUpdate();
            int cust4 = getGeneratedId(pc);
            pu.setString(1, "customer4"); pu.setString(2, "1234"); pu.setInt(3, cust4); pu.setObject(4, null); pu.executeUpdate();
            pa.setString(1, "SAV-003"); pa.setDouble(2, 150.0); pa.setString(3, "Main"); pa.setString(4, "SAVINGS");
            pa.setString(5, null); pa.setString(6, null); pa.setInt(7, cust4); pa.executeUpdate();
            pa.setString(1, "CHK-003"); pa.setDouble(2, 400.0); pa.setString(3, "Main"); pa.setString(4, "CHEQUE");
            pa.setString(5, "SmallBiz Pty"); pa.setString(6, "Gaborone"); pa.setInt(7, cust4); pa.executeUpdate();

            // Total accounts inserted: 10

            // Insert employees
            pe.setString(1, "John"); pe.setString(2, "Manager"); pe.setString(3, "john.manager@bank.com"); pe.setString(4, "MANAGER");
            pe.executeUpdate();
            int emp1 = getGeneratedId(pe);
            pu.setString(1, "employee1"); pu.setString(2, "emp123"); pu.setObject(3, null); pu.setInt(4, emp1); pu.executeUpdate();

            pe.setString(1, "Sarah"); pe.setString(2, "Teller"); pe.setString(3, "sarah.teller@bank.com"); pe.setString(4, "TELLER");
            pe.executeUpdate();
            int emp2 = getGeneratedId(pe);
            pu.setString(1, "admin"); pu.setString(2, "admin123"); pu.setObject(3, null); pu.setInt(4, emp2); pu.executeUpdate();
        }
    }

    /**
     * Gets the generated ID from a PreparedStatement after an INSERT.
     * 
     * @param ps The PreparedStatement that executed an INSERT
     * @return The generated ID
     * @throws SQLException if no ID was obtained
     */
    private static int getGeneratedId(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) return rs.getInt(1);
            else throw new SQLException("No ID obtained");
        }
    }

    /**
     * Closes database resources on application shutdown.
     * Currently SQLite doesn't require explicit connection pool cleanup,
     * but this method is provided for future extensibility.
     */
    public static void closeDataSource() {
        // SQLite JDBC doesn't require explicit connection pool cleanup
        // This method is provided for future extensibility if a connection pool is added
    }
}

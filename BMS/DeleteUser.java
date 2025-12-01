import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeleteUser {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:banking.db";
        String username = "ABM";

        try (Connection conn = DriverManager.getConnection(url)) {
            // First, check if user exists
            String checkSql = "SELECT user_id, username FROM users WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    System.out.println("Found user: " + rs.getString("username") + " (ID: " + userId + ")");

                    // Delete user's accounts first (foreign key constraint)
                    String deleteAccountsSql = "DELETE FROM accounts WHERE user_id = ?";
                    try (PreparedStatement deleteAcctsStmt = conn.prepareStatement(deleteAccountsSql)) {
                        deleteAcctsStmt.setInt(1, userId);
                        int accountsDeleted = deleteAcctsStmt.executeUpdate();
                        System.out.println("Deleted " + accountsDeleted + " account(s)");
                    }

                    // Delete user
                    String deleteUserSql = "DELETE FROM users WHERE user_id = ?";
                    try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSql)) {
                        deleteUserStmt.setInt(1, userId);
                        int usersDeleted = deleteUserStmt.executeUpdate();
                        System.out.println("Deleted " + usersDeleted + " user(s)");
                    }

                    System.out.println("User '" + username + "' successfully deleted!");
                } else {
                    System.out.println("User '" + username + "' not found in database.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

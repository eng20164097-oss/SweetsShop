package repository;

import db.DatabaseManager;
import java.sql.*;

/**
 * Layer 3: Data Access Layer
 * Repository for User-related database operations.
 */
public class UserRepository {

    public String authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT role FROM users WHERE name = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getString("role") : null;
        }
    }

    public ResultSet getAllUsers() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        return conn.createStatement().executeQuery("SELECT id, name, role FROM users");
    }

    public void addUser(String name, String role, String password) throws SQLException {
        String sql = "INSERT INTO users (name, role, password) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
        }
    }

    public void deleteUser(int id) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void updateUser(int id, String name, String role) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET name = ?, role = ? WHERE id = ?")) {
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
    }
        // جلب إجمالي عدد الموظفين
    public int getStaffCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = db.DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

}

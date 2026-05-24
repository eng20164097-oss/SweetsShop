package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the database connection and schema creation.
 */
public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:sweets_shop.db";

    public static Connection connect() {
    Connection conn = null;
    try {
        // سطر إضافي لضمان تحميل المكتبة 
        Class.forName("org.sqlite.JDBC"); 
        
        conn = DriverManager.getConnection(URL);
        System.out.println("Connection to SQLite has been established.");
    } catch (Exception e) { 
        System.out.println("Connection error: " + e.getMessage());
    }
    return conn;
}

    public static void initializeDatabase() {
        String userTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "role TEXT NOT NULL,"
                + "password TEXT NOT NULL"
                + ");";

        String productTable = "CREATE TABLE IF NOT EXISTS products ("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "price REAL NOT NULL,"
                + "stock INTEGER NOT NULL"
                + ");";
                
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(productTable);
            // إضافة مستخدم تجريبي (مدير) للاختبار
            stmt.execute("INSERT OR IGNORE INTO users (id, name, role, password) VALUES (1, 'admin', 'Manager', '123')");

            System.out.println("Database tables initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Table creation error: " + e.getMessage());
        }
    }
    
    // 2. ميثود جديدة للتحقق من بيانات الدخول (شرط أساسي للمنطق)
    public static String authenticateUser(String username, String password) {
        String query = "SELECT role FROM users WHERE name = ? AND password = ?";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role"); // يعيد دور المستخدم (Manager, Chef...)
            }
        } catch (SQLException e) {
            System.out.println("Auth Error: " + e.getMessage());
        }
        return null; // إذا لم يجد المستخدم
    }

}


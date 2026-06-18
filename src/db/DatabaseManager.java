package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The central manager for database operations.
 * Handles connectivity, schema initialization, and user security.
 */

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:sweets_shop.db";
/**
 * Establishes a connection to the SQLite database file.
 * Uses the JDBC driver to create a communication bridge.
 * @return A Connection object if successful, otherwise null.
 */

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

/**
 * Sets up the database structure by creating tables for users and products.
 * It also inserts a default test administrator account.
 */

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
    
    //  ميثود جديدة للتحقق من بيانات الدخول (شرط أساسي للمنطق)
/**
 * Validates user credentials against the database records.
 * Uses PreparedStatement to prevent SQL injection attacks.
 * @param username The username entered in the login form.
 * @param password The password entered in the login form.
 * @return The role of the user (e.g., Manager, Chef) if valid, or null if invalid.
 */

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
        /**
     * Fetches all products from the database.
     * Demonstrates data retrieval logic for the UI.
     */
    public static java.sql.ResultSet getAllProducts() {
        try {
            Connection conn = connect();
            String query = "SELECT * FROM products";
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            System.out.println("Error fetching products: " + e.getMessage());
            return null;
        }
    }
    /**
     * Inserts a new sweet product into the database.
     * Includes Exception Handling for data safety.
     */
    public static void addProduct(String name, double price, int stock) throws SQLException {
        String sql = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";
        try (Connection conn = connect(); 
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stock);
            pstmt.executeUpdate();
            System.out.println("Product added successfully!");
        }
    }
    /**
     * Fetches all users (Staff) from the database.
     */
    public static java.sql.ResultSet getAllUsers() {
        try {
            Connection conn = connect();
            return conn.createStatement().executeQuery("SELECT id, name, role FROM users");
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
            return null;
        }
    }

    /**
     * Adds a new staff member.
     */
    public static void addUser(String name, String role, String password) throws SQLException {
        String sql = "INSERT INTO users (name, role, password) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
        }
    }
    // 1. حذف منتج
    public static void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // 2. تعديل منتج (تغيير السعر والكمية)
    public static void updateProduct(int id, String name, double price, int stock) throws SQLException {
        String sql = "UPDATE products SET name = ?, price = ?, stock = ? WHERE id = ?";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stock);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        }
    }
        // 1. حذف موظف
    public static void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // 2. تعديل بيانات موظف
    public static void updateUser(int id, String name, String role) throws SQLException {
        String sql = "UPDATE users SET name = ?, role = ? WHERE id = ?";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
    }



}

